/**
 * profile.js
 *
 * Handles the user profile page functionality:
 * - Edit/cancel/save profile
 * - Profile picture selection and gallery
 * - Genre selection toggle
 * - Client-side form validation
 */

document.addEventListener("DOMContentLoaded", () => {
    // -----------------------
    // DOM elements
    // -----------------------
    const editBtn = document.getElementById("editBtn");
    const saveBtn = document.getElementById("saveBtn");
    const cancelBtn = document.getElementById("cancelBtn");
    const form = document.getElementById("profileForm");

    const usernameInput = form.querySelector("input[name='name']");
    const descriptionInput = form.querySelector("textarea[name='description']");
    const profilePic = document.getElementById("profilePic");
    const imageGallery = document.getElementById("imageGallery");
    const imageFileName = document.getElementById("imageFileName");
    const genreSelection = document.getElementById("genreSelection");
    const userGenres = document.getElementById("userGenres"); // may be null if no genres
    const body = document.body;

    // -----------------------
    // State
    // -----------------------
    let editMode = false;
    let originalUsername = usernameInput.value;
    const originalDescription = descriptionInput.value;
    const originalImage = profilePic.src;
    const originalBackground = body.style.backgroundImage;
    const originalGenres = Array.from(genreSelection.querySelectorAll("input[type='checkbox']")).map(cb => cb.checked);

    // -----------------------
    // Edit button
    // -----------------------
    editBtn.addEventListener("click", () => {
        usernameInput.removeAttribute("readonly");
        descriptionInput.removeAttribute("readonly");
        genreSelection.style.display = "block";
        if (userGenres) userGenres.style.display = "none";

        editBtn.style.display = "none";
        saveBtn.style.display = "inline-block";
        cancelBtn.style.display = "inline-block";
        editMode = true;

        // Update originalUsername on entering edit mode
        originalUsername = usernameInput.value.trim();
    });

    // -----------------------
    // Cancel button
    // -----------------------
    cancelBtn.addEventListener("click", () => {
        usernameInput.value = originalUsername;
        descriptionInput.value = originalDescription;
        profilePic.src = originalImage;
        body.style.backgroundImage = originalBackground;
        imageFileName.value = originalImage.split("/").pop();

        genreSelection.querySelectorAll("input[type='checkbox']").forEach((cb, idx) => cb.checked = originalGenres[idx]);

        usernameInput.setAttribute("readonly", true);
        descriptionInput.setAttribute("readonly", true);
        genreSelection.style.display = "none";
        if (userGenres) userGenres.style.display = "block";
        editBtn.style.display = "inline-block";
        saveBtn.style.display = "none";
        cancelBtn.style.display = "none";

        clearError(usernameInput);
        clearError(descriptionInput);
        editMode = false;
    });

    // -----------------------
    // Profile picture selection
    // -----------------------
    profilePic.addEventListener("click", () => {
        if (!editMode) return;
        imageGallery.style.display = imageGallery.style.display === "none" ? "flex" : "none";
    });

    imageGallery.querySelectorAll(".selectable-img").forEach(img => {
        img.addEventListener("click", () => {
            profilePic.src = img.src;
            body.style.backgroundImage = `url(${img.src})`;
            imageFileName.value = img.src.split("/").pop();
            imageGallery.style.display = "none";
        });
    });

    // -----------------------
    // Helper: check username availability
    // -----------------------
    async function checkUsernameAvailability(username) {
        if (!editMode || username === originalUsername) return false; // not changed
        try {
            const response = await fetch(`/check-username?username=${encodeURIComponent(username)}`);
            if (!response.ok) throw new Error("Failed to check username");
            return await response.json(); // true if taken
        } catch (err) {
            console.error("Username check failed:", err);
            return null; // indicate check failed
        }
    }

    // -----------------------
    // Username blur check
    // -----------------------
    usernameInput.addEventListener("blur", async () => {
        clearError(usernameInput);
        const username = usernameInput.value.trim();
        if (username.length < 3 || username.length > 32) return;

        const isTaken = await checkUsernameAvailability(username);
        if (isTaken) showError(usernameInput, "Username already exists. Choose a different one.");
    });

    // -----------------------
    // Form submission
    // -----------------------
    form.addEventListener("submit", async (e) => {
        e.preventDefault();
        clearError(usernameInput);
        clearError(descriptionInput);

        const username = usernameInput.value.trim();
        const description = descriptionInput.value.trim();
        descriptionInput.value = description; // update trimmed value

        let hasError = false;

        // Username checks
        if (username.length < 3) {
            showError(usernameInput, "Username must be at least 3 characters");
            hasError = true;
        }
        if (username.length > 32) {
            showError(usernameInput, "Username must not exceed 32 characters");
            hasError = true;
        }

        const usernameRegex = /^[a-zA-Z0-9._-]+$/;
        if (!usernameRegex.test(username)) {
            showError(
                usernameInput,
                "Username contains invalid characters. Only letters, numbers, dots (.), underscores (_), and hyphens (-) are allowed."
            );
            hasError = true;
        }

        // Description checks
        if (description.length > 250) {
            showError(descriptionInput, "Description cannot exceed 250 characters");
            hasError = true;
        }

        const descriptionInvalidChars = /[<>"'%;()&+]/;
        if (descriptionInvalidChars.test(description)) {
            showError(descriptionInput, "Description contains invalid characters: < > \" ' % ; ( ) & +");
            hasError = true;
        }

        // Stop submission if there are validation errors
        if (hasError) return;

        // Check availability only if username changed
        const isTaken = await checkUsernameAvailability(username);
        if (isTaken === true) return showError(usernameInput, "Username already exists. Choose a different one.");
        if (isTaken === null) return showError(usernameInput, "Could not verify username availability. Try again later.");

        // Submit form normally
        const formData = new FormData(form);
        try {
            const response = await fetch(form.action, { method: 'POST', body: formData });
            if (!response.ok) {
                alert("Error updating profile");
                return;
            }
            window.location.reload();
        } catch (err) {
            console.error(err);
            alert("Could not submit profile. Try again later.");
        }
    });

    // -----------------------
    // Error helpers
    // -----------------------
    function showError(input, message) {
        clearError(input);
        input.classList.add("input-error");
        const error = document.createElement("div");
        error.className = "error-message";
        error.textContent = message;
        input.insertAdjacentElement("afterend", error);
    }

    function clearError(input) {
        input.classList.remove("input-error");
        const nextEl = input.nextElementSibling;
        if (nextEl && nextEl.classList.contains("error-message")) nextEl.remove();
    }
});
