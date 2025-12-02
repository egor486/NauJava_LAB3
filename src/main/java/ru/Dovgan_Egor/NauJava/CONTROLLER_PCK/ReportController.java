package ru.Dovgan_Egor.NauJava.CONTROLLER_PCK;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.Dovgan_Egor.NauJava.MODEL.ENTITY_PCK.Report;
import ru.Dovgan_Egor.NauJava.MODEL.ENTITY_PCK.ReportStatus;
import ru.Dovgan_Egor.NauJava.SERVICE_NEW_PCK.ReportService;

@RestController
@RequestMapping("/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @PostMapping("/create")
    public String createReport(){
        Long id = reportService.createReport();

        reportService.generateReport(id);

        return "Запустился отчет. ID = " + id;
    }

    @GetMapping("/{id}")
    public String getReport(@PathVariable Long id){
        Report report = reportService.getReport(id);

        if (report == null)
            return "Отчет не найден";

        if (report.getStatus() == ReportStatus.CREATED)
            return "Отчет все еще создается";

        if (report.getStatus() == ReportStatus.ERROR)
            return "Ошибка генерации: " + report.getContent();

        return report.getContent();
    }
}
