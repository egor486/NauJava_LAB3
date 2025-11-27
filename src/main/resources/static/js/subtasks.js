function openSubTaskDeleteModal(btn) {
    const subTaskId = btn.getAttribute("data-id");

    const modal = document.getElementById("subTaskDeleteModal");
    const form = document.getElementById("subTaskDeleteForm");

    form.action = "/subtasks/" + subTaskId + "/delete";
    modal.style.display = "flex";
}

function closeSubTaskDeleteModal() {
    document.getElementById("subTaskDeleteModal").style.display = "none";
}