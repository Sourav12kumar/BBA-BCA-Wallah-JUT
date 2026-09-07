package in.bbabca.wallah.controller;

import in.bbabca.wallah.model.*;
import in.bbabca.wallah.repository.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@Controller
public class PublicController {
    private final AcademicResourceRepository resourceRepository;
    private final NoticeRepository noticeRepository;
    private final SubjectRepository subjectRepository;

    public PublicController(AcademicResourceRepository resourceRepository,
                            NoticeRepository noticeRepository,
                            SubjectRepository subjectRepository) {
        this.resourceRepository = resourceRepository;
        this.noticeRepository = noticeRepository;
        this.subjectRepository = subjectRepository;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("pinnedNotices", noticeRepository.findTop5ByPinnedTrueAndActiveTrueOrderByNoticeDateDescCreatedAtDesc());
        model.addAttribute("notices", noticeRepository.findByActiveTrueOrderByPinnedDescNoticeDateDescCreatedAtDesc().stream().limit(5).toList());
        model.addAttribute("featuredResources", resourceRepository.findTop6ByFeaturedTrueAndActiveTrueOrderByCreatedAtDesc());
        model.addAttribute("resources", resourceRepository.findByActiveTrueOrderByFeaturedDescCreatedAtDesc().stream().limit(8).toList());
        model.addAttribute("popularResources", resourceRepository.findTop6ByActiveTrueAndFileUrlIsNotNullOrderByDownloadCountDescCreatedAtDesc());
        return "index";
    }

    @GetMapping("/resource/{id}/download")
    public String downloadResource(@PathVariable Long id) {
        AcademicResource resource = resourceRepository.findById(id).orElseThrow();
        if (!resource.isActive() || resource.getFileUrl() == null || resource.getFileUrl().isBlank()) {
            return "redirect:/resources";
        }
        resourceRepository.incrementDownloadCount(id);
        return "redirect:" + resource.getFileUrl();
    }

    @GetMapping("/subjects")
    public String subjects(@RequestParam Course course,
                           @RequestParam Integer semester,
                           Model model) {
        model.addAttribute("course", course);
        model.addAttribute("semester", semester);
        model.addAttribute("subjects", subjectRepository.findByCourseAndSemesterAndActiveTrueOrderByNameAsc(course, semester));
        return "subjects";
    }

    @GetMapping("/subject/{id}")
    public String subjectDashboard(@PathVariable Long id, Model model) {
        Subject subject = subjectRepository.findById(id).orElseThrow();
        Map<ResourceType, java.util.List<AcademicResource>> grouped = new LinkedHashMap<>();
        for (ResourceType type : ResourceType.values()) {
            var items = resourceRepository.findBySubjectIdAndTypeAndActiveTrueOrderByFeaturedDescCreatedAtDesc(id, type);
            if (!items.isEmpty()) grouped.put(type, items);
        }
        model.addAttribute("subject", subject);
        model.addAttribute("groupedResources", grouped);
        return "subject-dashboard";
    }

    @GetMapping("/resources")
    public String resources(@RequestParam(required=false) Course course,
                            @RequestParam(required=false) Integer semester,
                            @RequestParam(required=false) ResourceType type,
                            @RequestParam(required=false) String q,
                            Model model) {
        var results = resourceRepository.findByActiveTrueOrderByFeaturedDescCreatedAtDesc();
        if (q != null && !q.isBlank()) {
            results = resourceRepository.findByTitleContainingIgnoreCaseAndActiveTrueOrderByFeaturedDescCreatedAtDesc(q);
        } else if (course != null && semester != null && type != null) {
            results = resourceRepository.findByCourseAndSemesterAndTypeAndActiveTrueOrderByFeaturedDescCreatedAtDesc(course, semester, type);
        } else if (course != null && semester != null) {
            results = resourceRepository.findByCourseAndSemesterAndActiveTrueOrderByFeaturedDescCreatedAtDesc(course, semester);
        }
        model.addAttribute("resources", results);
        model.addAttribute("courses", Course.values());
        model.addAttribute("types", ResourceType.values());
        return "resources";
    }

    @GetMapping("/notices")
    public String notices(Model model) {
        model.addAttribute("notices", noticeRepository.findByActiveTrueOrderByPinnedDescNoticeDateDescCreatedAtDesc());
        return "notices";
    }

    @GetMapping("/about")
    public String about() { return "about"; }
}
