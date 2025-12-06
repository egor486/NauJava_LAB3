package ru.Dovgan_Egor.NauJava.TEST;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.Dovgan_Egor.NauJava.MODEL.CRUD_REPOS_PCK.ReportRepository;
import ru.Dovgan_Egor.NauJava.MODEL.CRUD_REPOS_PCK.UserRepository;
import ru.Dovgan_Egor.NauJava.MODEL.ENTITY_PCK.Report;
import ru.Dovgan_Egor.NauJava.MODEL.ENTITY_PCK.ReportStatus;
import ru.Dovgan_Egor.NauJava.MODEL.ENTITY_PCK.Task;
import ru.Dovgan_Egor.NauJava.MODEL.REPOSITORY_PCK.TaskRestRepository;
import ru.Dovgan_Egor.NauJava.SERVICE.ReportService;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit тесты для ReportService с использованием Mockito.
 * Покрытие методов создания и асинхронной генерации отчетов.
 */
@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TaskRestRepository taskRestRepository;

    @InjectMocks
    private ReportService reportService;

    private Report testReport;

    @BeforeEach
    void setup() {
        testReport = new Report();
        testReport.setId(1L);
        testReport.setStatus(ReportStatus.CREATED);
        testReport.setContent("Отчет начал формироваться ...");
    }

    @Test
    void testCreateReport_success() {
        // Настройка мока: при сохранении проставляем ID в переданный объект
        when(reportRepository.save(any(Report.class))).thenAnswer(invocation -> {
            Report r = invocation.getArgument(0);
            r.setId(1L);
            return r;
        });

        // Вызов метода
        Long reportId = reportService.createReport();

        // Проверки
        assertNotNull(reportId);
        assertEquals(1L, reportId);
        
        verify(reportRepository, times(1)).save(any(Report.class));
        verify(reportRepository).save(argThat(report ->
                report.getStatus() == ReportStatus.CREATED &&
                report.getContent().contains("формироваться")
        ));
    }

    @Test
    void testGetReport_found() {
        // Настройка мока
        when(reportRepository.findById(1L)).thenReturn(Optional.of(testReport));

        // Вызов метода
        Report result = reportService.getReport(1L);

        // Проверки
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(ReportStatus.CREATED, result.getStatus());
        
        verify(reportRepository, times(1)).findById(1L);
    }

    @Test
    void testGetReport_notFound() {
        // Настройка мока
        when(reportRepository.findById(999L)).thenReturn(Optional.empty());

        // Вызов метода
        Report result = reportService.getReport(999L);

        // Проверки
        assertNull(result);
        
        verify(reportRepository, times(1)).findById(999L);
    }

    @Test
    void testGenerateReport_success() throws Exception {
        // Подготовка тестовых данных
        Task task1 = new Task();
        task1.setName("Task 1");
        task1.setDescription("Description 1");

        Task task2 = new Task();
        task2.setName("Task 2");
        task2.setDescription("Description 2");

        // Настройка моков
        when(reportRepository.findById(1L))
                .thenReturn(Optional.of(testReport))
                .thenReturn(Optional.of(testReport));
        when(userRepository.count()).thenReturn(10L);
        when(taskRestRepository.findAll()).thenReturn(Arrays.asList(task1, task2));
        when(reportRepository.save(any(Report.class))).thenReturn(testReport);

        // Вызов метода (асинхронный)
        reportService.generateReport(1L);

        // Ждем завершения асинхронной операции
        Thread.sleep(500);

        // Проверки
        verify(reportRepository, atLeastOnce()).findById(1L);
        verify(userRepository, times(1)).count();
        verify(taskRestRepository, times(1)).findAll();
        verify(reportRepository, times(1)).save(any(Report.class));
    }

    @Test
    void testGenerateReport_error() throws Exception {
        // Настройка мока для ошибки
        Report errorReport = new Report();
        errorReport.setId(1L);

        when(reportRepository.findById(1L))
                .thenThrow(new RuntimeException("Database error")) // первый вызов внутри try
                .thenReturn(java.util.Optional.of(errorReport)); // повторный вызов в блоке catch

        when(reportRepository.save(any(Report.class)))
                .thenAnswer(invocation -> {
                    Report r = invocation.getArgument(0);
                    return r;
                });

        // Вызов метода (асинхронный)
        reportService.generateReport(1L);

        // Ждем завершения асинхронной операции
        Thread.sleep(500);

        // Проверяем, что была попытка найти отчет (дважды: в try и в catch)
        verify(reportRepository, atLeast(2)).findById(1L);

        // Проверяем, что отчет сохранен со статусом ERROR
        verify(reportRepository, atLeastOnce()).save(argThat(r -> r.getStatus() == ReportStatus.ERROR));
    }

    @Test
    void testGenerateReport_containsUserCount() throws Exception {
        // Подготовка
        Report completedReport = new Report();
        completedReport.setId(1L);
        completedReport.setStatus(ReportStatus.COMPLETED);
        completedReport.setContent("<h1>Отчёт статистики</h1><p><b>Количество пользователей:</b> 5</p>");

        when(reportRepository.findById(1L))
                .thenReturn(Optional.of(testReport))
                .thenReturn(Optional.of(completedReport));
        when(userRepository.count()).thenReturn(5L);
        when(taskRestRepository.findAll()).thenReturn(Arrays.asList());
        when(reportRepository.save(any(Report.class))).thenReturn(completedReport);

        // Вызов
        reportService.generateReport(1L);
        Thread.sleep(500);

        // Проверки
        verify(reportRepository, times(1)).save(argThat(report -> {
            if (report.getStatus() == ReportStatus.COMPLETED) {
                return report.getContent().contains("Количество пользователей");
            }
            return true;
        }));
    }

    @Test
    void testGenerateReport_containsTaskList() throws Exception {
        // Подготовка
        Task task = new Task();
        task.setName("Test Task");
        task.setDescription("Test Description");

        Report completedReport = new Report();
        completedReport.setId(1L);
        completedReport.setStatus(ReportStatus.COMPLETED);

        when(reportRepository.findById(1L))
                .thenReturn(Optional.of(testReport))
                .thenReturn(Optional.of(completedReport));
        when(userRepository.count()).thenReturn(1L);
        when(taskRestRepository.findAll()).thenReturn(Arrays.asList(task));
        when(reportRepository.save(any(Report.class))).thenReturn(completedReport);

        // Вызов
        reportService.generateReport(1L);
        Thread.sleep(500);

        // Проверки
        verify(taskRestRepository, times(1)).findAll();
        verify(reportRepository, times(1)).save(any(Report.class));
    }
}

