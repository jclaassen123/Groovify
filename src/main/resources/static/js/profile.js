document.addEventListener("DOMContentLoaded", () => {
    const editBtn = document.getElementById("editBtn");
    const saveBtn = document.getElementById("saveBtn");
    const cancelBtn = document.getElementById("cancelBtn");
    const form = document.getElementById("profileForm");

    const usernameInput = form.querySelector("input[name='name']");
    const descriptionInput = form.querySelector("textarea[name='description']");
    const profilePic = document.getElementById("profilePic");
    const imageGallery = document.getElementById("imageGallery");
    const imageFileName = document.getElementById("imageFileName");
    const body = document.body;

    let originalUsername = usernameInput.value;
    let originalDescription = descriptionInput.value;
    let originalImage = profilePic.src;
    let originalBackground = body.style.backgroundImage;
    let editMode = false;

    // --- Utility functions for errors ---
    function showError(inputElement, message, count) {
        clearError(inputElement);
        inputElement.classList.add("input-error");

        const error = document.createElement("div");
        error.className = "error-message";
        error.textContent = count !== undefined ? `${message} (current: ${count})` : message;
        inputElement.insertAdjacentElement("afterend", error);
    }

    function clearError(inputElement) {
        inputElement.classList.remove("input-error");
        const nextEl = inputElement.nextElementSibling;
        if (nextEl && nextEl.classList.contains("error-message")) {
            nextEl.remove();
        }
    }

    // --- Enable editing ---
    editBtn.addEventListener("click", () => {
        usernameInput.removeAttribute("readonly");
        descriptionInput.removeAttribute("readonly");

        editBtn.style.display = "none";
        saveBtn.style.display = "inline-block";
        cancelBtn.style.display = "inline-block";
        editMode = true;
    });

    // --- Cancel editing ---
    cancelBtn.addEventListener("click", () => {
        usernameInput.value = originalUsername;
        descriptionInput.value = originalDescription;
        profilePic.src = originalImage;
        body.style.backgroundImage = originalBackground;
        imageFileName.value = originalImage.split("/").pop();

        usernameInput.setAttribute("readonly", true);
        descriptionInput.setAttribute("readonly", true);

        editBtn.style.display = "inline-block";
        saveBtn.style.display = "none";
        cancelBtn.style.display = "none";
        imageGallery.style.display = "none";


        clearError(usernameInput);
        clearError(descriptionInput);
        editMode = false;
    });

    // --- Open image gallery ---
    profilePic.addEventListener("click", () => {
        if (!editMode) return;
        imageGallery.style.display = imageGallery.style.display === "none" ? "flex" : "none";
    });

    // --- Select image from gallery ---
    imageGallery.querySelectorAll(".selectable-img").forEach(img => {
        img.addEventListener("click", () => {
            profilePic.src = img.src;
            body.style.backgroundImage = `url(${img.src})`;
            imageFileName.value = img.src.split("/").pop();
            imageGallery.style.display = "none";
        });
    });

    // --- Form submit (validate + check username availability) ---
    form.addEventListener("submit", async (e) => {
        e.preventDefault();
        clearError(usernameInput);
        clearError(descriptionInput);

        usernameInput.value = usernameInput.value.trim();
        descriptionInput.value = descriptionInput.value.trim();

        const username = usernameInput.value;
        const description = descriptionInput.value;
        let hasError = false;

        // Username validation (3–32 chars)
        if (username.length < 3 || username.length > 32) {
            showError(usernameInput, "Username must be between 3 and 32 characters", username.length);
            // Reset to original username
            usernameInput.value = originalUsername;
            hasError = true;
        }

        // Description validation (max 250 chars)
        if (description.length > 250) {
            showError(descriptionInput, "Description cannot exceed 250 characters", description.length);
            hasError = true;
        }

        if (hasError) return;

        // Username uniqueness check (if changed)
        if (username !== originalUsername) {
            try {
                const response = await fetch(`/check-username?username=${encodeURIComponent(username)}`);
                const exists = await response.json();

                if (exists) {
                    showError(usernameInput, "That username is already taken. Please choose another.", username.length);
                    return;
                }
            } catch (error) {
                console.error("Error checking username:", error);
                showError(usernameInput, "Could not verify username availability. Try again later.", username.length);
                return;
            }
        }

        // ✅ Passed all checks
        form.submit();

        originalUsername = username;
        originalDescription = description;
        originalImage = profilePic.src;
        originalBackground = body.style.backgroundImage;
    });
});