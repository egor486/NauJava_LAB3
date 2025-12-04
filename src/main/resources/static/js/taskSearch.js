document.getElementById("searchInput").addEventListener("input", function () {
    let query = this.value;

    fetch("/tasks-page?search=" + encodeURIComponent(query))
        .then(response => response.text())
        .then(html => {
            // Вставляем фрагмент таблицы из ответа
            document.getElementById("tasksTable").innerHTML = html;
        });
});
