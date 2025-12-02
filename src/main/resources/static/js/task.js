document.addEventListener('DOMContentLoaded', function () {
    const subTaskForm = document.getElementById('addSubTaskForm');
    const taskForm = document.getElementById('editTaskForm');

    subTaskForm.addEventListener('submit', function () {
        subTaskForm.querySelector('input[name="taskName"]').value = taskForm.querySelector('input[name="name"]').value;
        subTaskForm.querySelector('input[name="taskDescription"]').value = taskForm.querySelector('textarea[name="description"]').value;
        subTaskForm.querySelector('input[name="dt_beg"]').value = taskForm.querySelector('input[name="dt_beg"]').value;
        subTaskForm.querySelector('input[name="dt_end"]').value = taskForm.querySelector('input[name="dt_end"]').value;
        subTaskForm.querySelector('input[name="statusId"]').value = taskForm.querySelector('select[name="status_id"]').value;
    });
});