function openTaskDeleteModal(btn) {
    const taskId = btn.getAttribute("data-id");

    const modal = document.getElementById("taskDeleteModal");
    const form = document.getElementById("taskDeleteForm");

    form.action = "/tasks/delete/" + taskId;
    modal.style.display = "flex";
}

function closeTaskDeleteModal() {
    document.getElementById("taskDeleteModal").style.display = "none";
}