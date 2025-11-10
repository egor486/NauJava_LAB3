package ru.Dovgan_Egor.NauJava.SERVICE_NEW_PCK;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.Dovgan_Egor.NauJava.CRUD_REPOS_PCK.ReportRepository;
import ru.Dovgan_Egor.NauJava.CRUD_REPOS_PCK.UserRepository;
import ru.Dovgan_Egor.NauJava.ENTITY_PCK.Report;
import ru.Dovgan_Egor.NauJava.ENTITY_PCK.ReportStatus;
import ru.Dovgan_Egor.NauJava.ENTITY_PCK.Task;
import ru.Dovgan_Egor.NauJava.REPOSITORY_PCK.TaskRestRepository;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class ReportService {

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRestRepository taskRestRepository;

    public Long createReport(){
        Report report = new Report();
        report.setStatus(ReportStatus.CREATED);
        report.setContent("Отчет начал формироваться ...");
        reportRepository.save(report);

        return report.getId();
    }


    @Async
    public CompletableFuture<Void> generateReport(Long reportId) {

        return CompletableFuture.runAsync(() -> {
            try {
                Report report = reportRepository.findById(reportId).orElseThrow();

                long startTotal = System.currentTimeMillis();


                long startUsers = System.currentTimeMillis();
                CompletableFuture<Long> usersCountFuture = CompletableFuture.supplyAsync(() -> {
                    return userRepository.count();
                });
                long usersCount = usersCountFuture.join();
                long usersTime = System.currentTimeMillis() - startUsers;


                long startTasks = System.currentTimeMillis();
                CompletableFuture<List<Task>> tasksFuture = CompletableFuture.supplyAsync(() -> {
                    return taskRestRepository.findAll();
                });
                List<Task> tasks = tasksFuture.join();
                long tasksTime = System.currentTimeMillis() - startTasks;


                long totalTime = System.currentTimeMillis() - startTotal;


                StringBuilder html = new StringBuilder();
                html.append("<h1>Отчёт статистики</h1>");
                html.append("<p><b>Количество пользователей:</b> ").append(usersCount).append("</p>");
                html.append("<p>Время подсчёта пользователей: ").append(usersTime).append(" ms</p>");

                html.append("<h2>Список задач</h2>");
                html.append("<ul>");
                for (Task t : tasks) {
                    html.append("<li>").append(t.getName())
                            .append(" (").append(t.getDescription()).append(")").append("</li>");
                }
                html.append("</ul>");
                html.append("<p>Время получения списка задач: ").append(tasksTime).append(" ms</p>");

                html.append("<hr>");
                html.append("<p><b>Общее время формирования отчёта:</b> ").append(totalTime).append(" ms</p>");


                report.setContent(html.toString());
                report.setStatus(ReportStatus.COMPLETED);
                reportRepository.save(report);

            } catch (Exception e) {
                Report rep = reportRepository.findById(reportId).orElse(null);
                if (rep != null) {
                    rep.setStatus(ReportStatus.ERROR);
                    rep.setContent("Ошибка генерации отчета: " + e.getMessage());
                    reportRepository.save(rep);
                }
            }
        });
    }

    public Report getReport(Long id){
        return reportRepository.findById(id).orElse(null);
    }


}
