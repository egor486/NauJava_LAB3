document.addEventListener('DOMContentLoaded', function () {
    const toggleForms = document.querySelectorAll('.subtask-toggle-form');
    const taskForm = document.getElementById('editTaskForm');

    toggleForms.forEach(form => {
        const checkbox = form.querySelector('input[type="checkbox"]');
        checkbox.addEventListener('change', function (event) {
            event.preventDefault();

            // добавляем hidden-поля с данными задачи
            const fields = [
                { name: 'taskName', value: taskForm.querySelector('input[name="name"]').value },
                { name: 'taskDescription', value: taskForm.querySelector('textarea[name="description"]').value },
                { name: 'dt_beg', value: taskForm.querySelector('input[name="dt_beg"]').value },
                { name: 'dt_end', value: taskForm.querySelector('input[name="dt_end"]').value },
                { name: 'statusId', value: taskForm.querySelector('select[name="status_id"]').value }
            ];

            fields.forEach(f => {
                let input = form.querySelector(`input[name="${f.name}"]`);
                if (!input) {
                    input = document.createElement('input');
                    input.type = 'hidden';
                    input.name = f.name;
                    form.appendChild(input);
                }
                input.value = f.value;
            });

            form.submit();
        });
    });
});
