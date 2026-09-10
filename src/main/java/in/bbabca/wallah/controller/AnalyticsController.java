package in.bbabca.wallah.controller;

import in.bbabca.wallah.repository.AcademicResourceRepository;
import in.bbabca.wallah.repository.DownloadEventRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/admin/analytics")
public class AnalyticsController {

    private final DownloadEventRepository downloadEventRepository;
    private final AcademicResourceRepository resourceRepository;

    public AnalyticsController(DownloadEventRepository downloadEventRepository,
                               AcademicResourceRepository resourceRepository) {
        this.downloadEventRepository = downloadEventRepository;
        this.resourceRepository = resourceRepository;
    }

    @GetMapping
    public String analytics(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            Model model) {

        LocalDate today = LocalDate.now();
        LocalDate selectedEnd = end == null ? today : end;
        LocalDate selectedStart = start == null ? selectedEnd.minusDays(29) : start;

        if (selectedStart.isAfter(selectedEnd)) {
            LocalDate temp = selectedStart;
            selectedStart = selectedEnd;
            selectedEnd = temp;
        }

        LocalDateTime rangeStart = selectedStart.atStartOfDay();
        LocalDateTime rangeEnd = selectedEnd.plusDays(1).atStartOfDay().minusNanos(1);

        long rangeDownloads = downloadEventRepository.countByDownloadedAtBetween(rangeStart, rangeEnd);
        long todayDownloads = downloadEventRepository.countByDownloadedAtBetween(
                today.atStartOfDay(), today.plusDays(1).atStartOfDay().minusNanos(1));
        long sevenDayDownloads = downloadEventRepository.countByDownloadedAtBetween(
                today.minusDays(6).atStartOfDay(), today.plusDays(1).atStartOfDay().minusNanos(1));
        long thirtyDayDownloads = downloadEventRepository.countByDownloadedAtBetween(
                today.minusDays(29).atStartOfDay(), today.plusDays(1).atStartOfDay().minusNanos(1));

        model.addAttribute("selectedStart", selectedStart);
        model.addAttribute("selectedEnd", selectedEnd);
        model.addAttribute("today", today);
        model.addAttribute("sevenDayStart", today.minusDays(6));
        model.addAttribute("thirtyDayStart", today.minusDays(29));
        model.addAttribute("rangeDownloads", rangeDownloads);
        model.addAttribute("todayDownloads", todayDownloads);
        model.addAttribute("sevenDayDownloads", sevenDayDownloads);
        model.addAttribute("thirtyDayDownloads", thirtyDayDownloads);
        model.addAttribute("lifetimeDownloads", resourceRepository.getTotalDownloads());
        model.addAttribute("topResources", downloadEventRepository.findTopResourcesBetween(rangeStart, rangeEnd)
                .stream().limit(10).toList());

        return "admin/analytics";
    }
}
