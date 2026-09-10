package in.bbabca.wallah.controller;

import in.bbabca.wallah.model.*;
import in.bbabca.wallah.repository.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Controller
public class PublicController {
    private static final int PAGE_SIZE = 12;

    private final AcademicResourceRepository resourceRepository;
    private final NoticeRepository noticeRepository;
    private final SubjectRepository subjectRepository;
    private final OpportunityRepository opportunityRepository;
    private final DownloadEventRepository downloadEventRepository;

    public PublicController(AcademicResourceRepository resourceRepository,
                            NoticeRepository noticeRepository,
                            SubjectRepository subjectRepository,
                            OpportunityRepository opportunityRepository,
                            DownloadEventRepository downloadEventRepository) {
        this.resourceRepository = resourceRepository;
        this.noticeRepository = noticeRepository;
        this.subjectRepository = subjectRepository;
        this.opportunityRepository = opportunityRepository;
        this.downloadEventRepository = downloadEventRepository;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("pinnedNotices", noticeRepository.findTop5ByPinnedTrueAndActiveTrueOrderByNoticeDateDescCreatedAtDesc());
        model.addAttribute("notices", noticeRepository.findByActiveTrueOrderByPinnedDescNoticeDateDescCreatedAtDesc().stream().limit(5).toList());
        model.addAttribute("featuredResources", resourceRepository.findTop6ByFeaturedTrueAndActiveTrueOrderByCreatedAtDesc());
        model.addAttribute("resources", resourceRepository.findByActiveTrueOrderByFeaturedDescCreatedAtDesc().stream().limit(8).toList());
        model.addAttribute("popularResources", resourceRepository.findTop6ByActiveTrueAndFileUrlIsNotNullOrderByDownloadCountDescCreatedAtDesc());
        model.addAttribute("opportunities", opportunityRepository.findTop6ByActiveTrueOrderByFeaturedDescCreatedAtDesc());
        return "index";
    }

    @GetMapping("/placements")
    public String placements(@RequestParam(required = false) String course,
                             @RequestParam(required = false) String batch,
                             @RequestParam(required = false) String location,
                             @RequestParam(required = false) String q,
                             @RequestParam(defaultValue = "featured") String sort,
                             @RequestParam(defaultValue = "0") int page,
                             Model model) {
        return opportunityPage(OpportunityType.PLACEMENT, course, batch, location, q, sort, page, model,
                "Placement Opportunities",
                "Latest placement and job opportunities for BBA and BCA students.");
    }

    @GetMapping("/internships")
    public String internships(@RequestParam(required = false) String course,
                              @RequestParam(required = false) String batch,
                              @RequestParam(required = false) String location,
                              @RequestParam(required = false) String q,
                              @RequestParam(defaultValue = "featured") String sort,
                              @RequestParam(defaultValue = "0") int page,
                              Model model) {
        return opportunityPage(OpportunityType.INTERNSHIP, course, batch, location, q, sort, page, model,
                "Internship Opportunities",
                "Internship opportunities, training programs and student hiring updates for BBA and BCA students.");
    }

    @GetMapping("/opportunities")
    public String opportunities(@RequestParam(required = false) OpportunityType type,
                                @RequestParam(required = false) String course,
                                @RequestParam(required = false) String batch,
                                @RequestParam(required = false) String location,
                                @RequestParam(required = false) String q,
                                @RequestParam(defaultValue = "featured") String sort,
                                @RequestParam(defaultValue = "0") int page,
                                Model model) {
        return opportunityPage(type, course, batch, location, q, sort, page, model,
                type == null ? "Career Opportunities" : (type == OpportunityType.PLACEMENT ? "Placement Opportunities" : "Internship Opportunities"),
                "Filter placement and internship opportunities by course, batch, location and keyword.");
    }

    private String opportunityPage(OpportunityType type,
                                   String course,
                                   String batch,
                                   String location,
                                   String q,
                                   String sort,
                                   int page,
                                   Model model,
                                   String pageTitle,
                                   String pageDescription) {
        List<Opportunity> all = type == null
                ? opportunityRepository.findByActiveTrueOrderByFeaturedDescDeadlineAscCreatedAtDesc()
                : opportunityRepository.findByTypeAndActiveTrueOrderByFeaturedDescDeadlineAscCreatedAtDesc(type);

        String courseNeedle = normalize(course);
        String batchNeedle = normalize(batch);
        String locationNeedle = normalize(location);
        String keyword = normalize(q);

        Comparator<Opportunity> comparator = switch (normalize(sort)) {
            case "newest" -> Comparator.comparing(Opportunity::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder()));
            case "deadline" -> Comparator.comparing(Opportunity::getDeadline, Comparator.nullsLast(Comparator.naturalOrder()))
                    .thenComparing(Opportunity::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder()));
            default -> Comparator.comparing(Opportunity::isFeatured).reversed()
                    .thenComparing(Opportunity::getDeadline, Comparator.nullsLast(Comparator.naturalOrder()))
                    .thenComparing(Opportunity::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder()));
        };

        List<Opportunity> filtered = all.stream()
                .filter(o -> courseNeedle.isBlank() || contains(o.getEligibleCourses(), courseNeedle))
                .filter(o -> batchNeedle.isBlank() || contains(o.getBatch(), batchNeedle))
                .filter(o -> locationNeedle.isBlank() || contains(o.getLocation(), locationNeedle))
                .filter(o -> keyword.isBlank()
                        || contains(o.getCompany(), keyword)
                        || contains(o.getRole(), keyword)
                        || contains(o.getDescription(), keyword)
                        || contains(o.getEligibility(), keyword))
                .sorted(comparator)
                .toList();

        PageSlice<Opportunity> slice = paginate(filtered, page);

        List<Opportunity> activeOpportunities = opportunityRepository.findByActiveTrueOrderByFeaturedDescDeadlineAscCreatedAtDesc();
        List<String> courses = activeOpportunities.stream()
                .flatMap(o -> splitValues(o.getEligibleCourses()).stream())
                .distinct().sorted().toList();
        List<String> batches = activeOpportunities.stream()
                .flatMap(o -> splitValues(o.getBatch()).stream())
                .distinct().sorted().toList();
        List<String> locations = activeOpportunities.stream()
                .map(Opportunity::getLocation)
                .filter(v -> v != null && !v.isBlank())
                .distinct().sorted().toList();

        model.addAttribute("pageTitle", pageTitle);
        model.addAttribute("pageDescription", pageDescription);
        model.addAttribute("opportunities", slice.items());
        model.addAttribute("totalResults", filtered.size());
        model.addAttribute("currentPage", slice.currentPage());
        model.addAttribute("totalPages", slice.totalPages());
        model.addAttribute("hasPrevious", slice.currentPage() > 0);
        model.addAttribute("hasNext", slice.currentPage() + 1 < slice.totalPages());
        model.addAttribute("opportunityType", type);
        model.addAttribute("types", OpportunityType.values());
        model.addAttribute("courseOptions", courses);
        model.addAttribute("batchOptions", batches);
        model.addAttribute("locationOptions", locations);
        model.addAttribute("selectedCourse", course);
        model.addAttribute("selectedBatch", batch);
        model.addAttribute("selectedLocation", location);
        model.addAttribute("selectedQuery", q);
        model.addAttribute("selectedType", type);
        model.addAttribute("selectedSort", normalize(sort).isBlank() ? "featured" : sort);
        return "opportunities";
    }

    @GetMapping("/resource/{id}/download")
    public String downloadResource(@PathVariable Long id) {
        AcademicResource resource = resourceRepository.findById(id).orElseThrow();
        if (!resource.isActive() || resource.getFileUrl() == null || resource.getFileUrl().isBlank()) {
            return "redirect:/resources";
        }
        resourceRepository.incrementDownloadCount(id);
        downloadEventRepository.save(new DownloadEvent(resource.getId(), resource.getTitle()));
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
                            @RequestParam(defaultValue = "featured") String sort,
                            @RequestParam(defaultValue = "0") int page,
                            Model model) {
        List<AcademicResource> results = resourceRepository.findByActiveTrueOrderByFeaturedDescCreatedAtDesc();
        if (q != null && !q.isBlank()) {
            results = resourceRepository.findByTitleContainingIgnoreCaseAndActiveTrueOrderByFeaturedDescCreatedAtDesc(q);
        } else if (course != null && semester != null && type != null) {
            results = resourceRepository.findByCourseAndSemesterAndTypeAndActiveTrueOrderByFeaturedDescCreatedAtDesc(course, semester, type);
        } else if (course != null && semester != null) {
            results = resourceRepository.findByCourseAndSemesterAndActiveTrueOrderByFeaturedDescCreatedAtDesc(course, semester);
        }

        Comparator<AcademicResource> comparator = switch (normalize(sort)) {
            case "newest" -> Comparator.comparing(AcademicResource::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder()));
            case "downloads" -> Comparator.comparingLong(AcademicResource::getDownloadCount).reversed()
                    .thenComparing(AcademicResource::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder()));
            case "title" -> Comparator.comparing(AcademicResource::getTitle, String.CASE_INSENSITIVE_ORDER);
            default -> Comparator.comparing(AcademicResource::isFeatured).reversed()
                    .thenComparing(AcademicResource::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder()));
        };

        List<AcademicResource> sorted = results.stream().sorted(comparator).toList();
        PageSlice<AcademicResource> slice = paginate(sorted, page);

        model.addAttribute("resources", slice.items());
        model.addAttribute("totalResults", sorted.size());
        model.addAttribute("currentPage", slice.currentPage());
        model.addAttribute("totalPages", slice.totalPages());
        model.addAttribute("hasPrevious", slice.currentPage() > 0);
        model.addAttribute("hasNext", slice.currentPage() + 1 < slice.totalPages());
        model.addAttribute("courses", Course.values());
        model.addAttribute("types", ResourceType.values());
        model.addAttribute("selectedCourse", course);
        model.addAttribute("selectedSemester", semester);
        model.addAttribute("selectedResourceType", type);
        model.addAttribute("selectedQuery", q);
        model.addAttribute("selectedSort", normalize(sort).isBlank() ? "featured" : sort);
        return "resources";
    }

    @GetMapping("/notices")
    public String notices(Model model) {
        model.addAttribute("notices", noticeRepository.findByActiveTrueOrderByPinnedDescNoticeDateDescCreatedAtDesc());
        return "notices";
    }

    @GetMapping("/about")
    public String about() { return "about"; }

    private static boolean contains(String value, String needle) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(needle);
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }

    private static List<String> splitValues(String value) {
        if (value == null || value.isBlank()) return List.of();
        return java.util.Arrays.stream(value.split("[,/|]"))
                .map(String::trim)
                .filter(v -> !v.isBlank())
                .toList();
    }

    private static <T> PageSlice<T> paginate(List<T> items, int requestedPage) {
        int totalPages = items.isEmpty() ? 0 : (int) Math.ceil(items.size() / (double) PAGE_SIZE);
        int page = Math.max(0, requestedPage);
        if (totalPages > 0) page = Math.min(page, totalPages - 1);
        int start = Math.min(page * PAGE_SIZE, items.size());
        int end = Math.min(start + PAGE_SIZE, items.size());
        return new PageSlice<>(items.subList(start, end), page, totalPages);
    }

    private record PageSlice<T>(List<T> items, int currentPage, int totalPages) {}
}
