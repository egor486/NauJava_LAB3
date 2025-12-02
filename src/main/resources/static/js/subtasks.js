function openSubTaskDeleteModal(btn) {
    const subTaskId = btn.getAttribute("data-id");
    const modal = document.getElementById("subTaskDeleteModal");
    const form = document.getElementById("subTaskDeleteForm");
    form.action = "/subtasks/" + subTaskId + "/delete";

        const taskForm = document.getElementById("editTaskForm");
        const hiddenFields = [
            { name: 'taskName', value: taskForm.querySelector('input[name="name"]').value },
            { name: 'taskDescription', value: taskForm.querySelector('textarea[name="description"]').value },
            { name: 'dt_beg', value: taskForm.querySelector('input[name="dt_beg"]').value },
            { name: 'dt_end', value: taskForm.querySelector('input[name="dt_end"]').value },
            { name: 'statusId', value: taskForm.querySelector('select[name="status_id"]').value }
        ];

        hiddenFields.forEach(f => {
            let input = form.querySelector(`input[name="${f.name}"]`);
            if (!input) {
                input = document.createElement('input');
                input.type = 'hidden';
                input.name = f.name;
                form.appendChild(input);
            }
            input.value = f.value;
        });

    modal.style.display = "flex";
}

function closeSubTaskDeleteModal() {
    document.getElementById("subTaskDeleteModal").style.display = "none";
}