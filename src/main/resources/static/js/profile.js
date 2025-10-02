document.addEventListener("DOMContentLoaded", () => {
    const editBtn = document.getElementById("editBtn");
    const saveBtn = document.getElementById("saveBtn");
    const cancelBtn = document.getElementById("cancelBtn");
    const form = document.getElementById("profileForm");

    // Store original values to revert on cancel
    const usernameInput = form.querySelector("input[name='name']");
    const descriptionInput = form.querySelector("textarea[name='description']");
    let originalUsername = usernameInput.value;
    let originalDescription = descriptionInput.value;

    // Enable editing
    editBtn.addEventListener("click", () => {
        usernameInput.removeAttribute("readonly");
        descriptionInput.removeAttribute("readonly");

        editBtn.style.display = "none";
        saveBtn.style.display = "inline-block";
        cancelBtn.style.display = "inline-block";
    });

    // Cancel editing
    cancelBtn.addEventListener("click", () => {
        usernameInput.value = originalUsername;
        descriptionInput.value = originalDescription;

        usernameInput.setAttribute("readonly", true);
        descriptionInput.setAttribute("readonly", true);

        editBtn.style.display = "inline-block";
        saveBtn.style.display = "none";
        cancelBtn.style.display = "none";
    });

    // Optionally, when the form submits, update the stored originals
    form.addEventListener("submit", () => {
        originalUsername = usernameInput.value;
        originalDescription = descriptionInput.value;
    });
});