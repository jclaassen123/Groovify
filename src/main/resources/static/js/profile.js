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

    // Enable editing
    editBtn.addEventListener("click", () => {
        usernameInput.removeAttribute("readonly");
        descriptionInput.removeAttribute("readonly");

        editBtn.style.display = "none";
        saveBtn.style.display = "inline-block";
        cancelBtn.style.display = "inline-block";

        editMode = true;
    });

    // Cancel editing
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
        editMode = false;
    });

    // Profile pic click opens gallery in edit mode
    profilePic.addEventListener("click", () => {
        if (!editMode) return;
        imageGallery.style.display = imageGallery.style.display === "none" ? "flex" : "none";
    });
    // Select an image from gallery
    imageGallery.querySelectorAll(".selectable-img").forEach(img => {
        img.addEventListener("click", () => {
            profilePic.src = img.src;
            body.style.backgroundImage = `url(${img.src})`; // Update background immediately
            imageFileName.value = img.src.split("/").pop();

            // Close the gallery automatically
            imageGallery.style.display = "none";
        });
    });

    // Update original values on save
    form.addEventListener("submit", () => {
        originalUsername = usernameInput.value;
        originalDescription = descriptionInput.value;
        originalImage = profilePic.src;
        originalBackground = body.style.backgroundImage;
    });
});