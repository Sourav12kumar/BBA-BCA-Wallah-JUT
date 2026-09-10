package in.bbabca.wallah.controller;

import in.bbabca.wallah.model.*;
import in.bbabca.wallah.repository.*;
import in.bbabca.wallah.service.FileStorageService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final AcademicResourceRepository resourceRepository;
    private final NoticeRepository noticeRepository;
    private final SubjectRepository subjectRepository;
    private final OpportunityRepository opportunityRepository;
    private final FileStorageService fileStorageService;

    public AdminController(AcademicResourceRepository resourceRepository,
                           NoticeRepository noticeRepository,
                           SubjectRepository subjectRepository,
                           OpportunityRepository opportunityRepository,
                           FileStorageService fileStorageService) {
        this.resourceRepository = resourceRepository;
        this.noticeRepository = noticeRepository;
        this.subjectRepository = subjectRepository;
        this.opportunityRepository = opportunityRepository;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping("/login")
    public String login() { return "admin/login"; }

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("resourceCount", resourceRepository.count());
        model.addAttribute("noticeCount", noticeRepository.count());
        model.addAttribute("subjectCount", subjectRepository.count());
        model.addAttribute("placementCount", opportunityRepository.countByType(OpportunityType.PLACEMENT));
        model.addAttribute("internshipCount", opportunityRepository.countByType(OpportunityType.INTERNSHIP));
        model.addAttribute("totalDownloads", resourceRepository.getTotalDownloads());
        model.addAttribute("popularResources", resourceRepository.findTop6ByActiveTrueAndFileUrlIsNotNullOrderByDownloadCountDescCreatedAtDesc());
        model.addAttribute("resources", resourceRepository.findAll());
        model.addAttribute("notices", noticeRepository.findAll());
        model.addAttribute("subjects", subjectRepository.findAll());
        model.addAttribute("opportunities", opportunityRepository.findAll());
        return "admin/dashboard";
    }

    @GetMapping("/opportunities/new")
    public String newOpportunity(Model model) {
        model.addAttribute("opportunity", new Opportunity());
        model.addAttribute("opportunityTypes", OpportunityType.values());
        return "admin/opportunity-form";
    }

    @PostMapping("/opportunities")
    public String saveOpportunity(@ModelAttribute Opportunity opportunity) {
        if (opportunity.getId() != null) {
            opportunityRepository.findById(opportunity.getId())
                    .ifPresent(existing -> opportunity.setCreatedAt(existing.getCreatedAt()));
        }
        opportunityRepository.save(opportunity);
        return "redirect:/admin";
    }

    @GetMapping("/opportunities/{id}/edit")
    public String editOpportunity(@PathVariable Long id, Model model) {
        model.addAttribute("opportunity", opportunityRepository.findById(id).orElseThrow());
        model.addAttribute("opportunityTypes", OpportunityType.values());
        return "admin/opportunity-form";
    }

    @PostMapping("/opportunities/{id}/delete")
    public String deleteOpportunity(@PathVariable Long id) {
        opportunityRepository.deleteById(id);
        return "redirect:/admin";
    }

    @GetMapping("/subjects/new")
    public String newSubject(Model model) {
        model.addAttribute("subject", new Subject());
        model.addAttribute("courses", Course.values());
        return "admin/subject-form";
    }

    @PostMapping("/subjects")
    public String saveSubject(@ModelAttribute Subject subject) {
        subjectRepository.save(subject);
        return "redirect:/admin";
    }

    @GetMapping("/subjects/{id}/edit")
    public String editSubject(@PathVariable Long id, Model model) {
        model.addAttribute("subject", subjectRepository.findById(id).orElseThrow());
        model.addAttribute("courses", Course.values());
        return "admin/subject-form";
    }

    @PostMapping("/subjects/{id}/delete")
    public String deleteSubject(@PathVariable Long id) {
        subjectRepository.deleteById(id);
        return "redirect:/admin";
    }

    @GetMapping("/resources/new")
    public String newResource(Model model) {
        prepareResourceForm(model, new AcademicResource());
        return "admin/resource-form";
    }

    @PostMapping("/resources")
    public String saveResource(@ModelAttribute AcademicResource resource,
                               @RequestParam(name = "file", required = false) MultipartFile file,
                               Model model) {
        try {
            AcademicResource existing = null;
            if (resource.getId() != null) {
                existing = resourceRepository.findById(resource.getId()).orElse(null);
            }

            if (resource.getSubject() == null || resource.getSubject().getId() == null) {
                throw new IllegalArgumentException("Please select a valid subject.");
            }

            Subject selectedSubject = subjectRepository.findById(resource.getSubject().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Selected subject does not exist."));
            resource.setSubject(selectedSubject);
            resource.setCourse(selectedSubject.getCourse());
            resource.setSemester(selectedSubject.getSemester());

            String previousUrl = existing != null ? existing.getFileUrl() : null;
            if (existing != null) {
                resource.setDownloadCount(existing.getDownloadCount());
                resource.setCreatedAt(existing.getCreatedAt());
            }

            if (file != null && !file.isEmpty()) {
                String storedName = fileStorageService.store(file);
                if (previousUrl != null && previousUrl.startsWith("/files/")) {
                    fileStorageService.deleteByPublicUrl(previousUrl);
                }
                resource.setFileUrl("/files/" + storedName);
            } else if ((resource.getFileUrl() == null || resource.getFileUrl().isBlank()) && previousUrl != null) {
                resource.setFileUrl(previousUrl);
            }

            resourceRepository.save(resource);
            return "redirect:/admin";
        } catch (IllegalArgumentException | IllegalStateException ex) {
            prepareResourceForm(model, resource);
            model.addAttribute("uploadError", ex.getMessage());
            return "admin/resource-form";
        }
    }

    @GetMapping("/resources/{id}/edit")
    public String editResource(@PathVariable Long id, Model model) {
        prepareResourceForm(model, resourceRepository.findById(id).orElseThrow());
        return "admin/resource-form";
    }

    @PostMapping("/resources/{id}/delete")
    public String deleteResource(@PathVariable Long id) {
        resourceRepository.findById(id).ifPresent(resource -> {
            fileStorageService.deleteByPublicUrl(resource.getFileUrl());
            resourceRepository.delete(resource);
        });
        return "redirect:/admin";
    }

    @GetMapping("/notices/new")
    public String newNotice(Model model) {
        model.addAttribute("notice", new Notice());
        return "admin/notice-form";
    }

    @PostMapping("/notices")
    public String saveNotice(@ModelAttribute Notice notice) {
        if (notice.getId() != null) {
            noticeRepository.findById(notice.getId())
                    .ifPresent(existing -> notice.setCreatedAt(existing.getCreatedAt()));
        }
        noticeRepository.save(notice);
        return "redirect:/admin";
    }

    @GetMapping("/notices/{id}/edit")
    public String editNotice(@PathVariable Long id, Model model) {
        model.addAttribute("notice", noticeRepository.findById(id).orElseThrow());
        return "admin/notice-form";
    }

    @PostMapping("/notices/{id}/delete")
    public String deleteNotice(@PathVariable Long id) {
        noticeRepository.deleteById(id);
        return "redirect:/admin";
    }

    private void prepareResourceForm(Model model, AcademicResource resource) {
        model.addAttribute("resource", resource);
        model.addAttribute("courses", Course.values());
        model.addAttribute("types", ResourceType.values());
        model.addAttribute("subjects", subjectRepository.findByActiveTrueOrderByCourseAscSemesterAscNameAsc());
    }
}
