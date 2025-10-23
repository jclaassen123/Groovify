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
    const genreSelection = document.getElementById("genreSelection");
    const userGenres = document.getElementById("userGenres");
    const body = document.body;

    let editMode = false;
    let originalUsername = usernameInput.value;
    let originalDescription = descriptionInput.value;
    let originalImage = profilePic.src;
    let originalBackground = body.style.backgroundImage;
    const originalGenres = Array.from(genreSelection.querySelectorAll("input[type='checkbox']")).map(cb => cb.checked);

    editBtn.addEventListener("click", () => {
        usernameInput.removeAttribute("readonly");
        descriptionInput.removeAttribute("readonly");
        genreSelection.style.display = "block";
        userGenres.style.display = "none";
        editBtn.style.display = "none";
        saveBtn.style.display = "inline-block";
        cancelBtn.style.display = "inline-block";
        editMode = true;
    });

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
        userGenres.style.display = "block";
        editBtn.style.display = "inline-block";
        saveBtn.style.display = "none";
        cancelBtn.style.display = "none";
        clearError(usernameInput);
        clearError(descriptionInput);
        editMode = false;
    });

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

    form.addEventListener("submit", async (e) => {
        e.preventDefault();
        clearError(usernameInput);
        clearError(descriptionInput);

        usernameInput.value = usernameInput.value.trim();
        descriptionInput.value = descriptionInput.value.trim();

        if (usernameInput.value.length < 3) return showError(usernameInput, "Username must be at least 3 characters");
        if (usernameInput.value.length > 32) return showError(usernameInput, "Username must not exceed 32 characters");
        if (descriptionInput.value.length > 250) return showError(descriptionInput, "Description cannot exceed 250 characters");

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