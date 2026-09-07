package in.bbabca.wallah.controller;

import in.bbabca.wallah.model.*;
import in.bbabca.wallah.repository.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final AcademicResourceRepository resourceRepository;
    private final NoticeRepository noticeRepository;
    private final SubjectRepository subjectRepository;

    public AdminController(AcademicResourceRepository resourceRepository,
                           NoticeRepository noticeRepository,
                           SubjectRepository subjectRepository) {
        this.resourceRepository = resourceRepository;
        this.noticeRepository = noticeRepository;
        this.subjectRepository = subjectRepository;
    }

    @GetMapping("/login")
    public String login() { return "admin/login"; }

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("resourceCount", resourceRepository.count());
        model.addAttribute("noticeCount", noticeRepository.count());
        model.addAttribute("subjectCount", subjectRepository.count());
        model.addAttribute("resources", resourceRepository.findAll());
        model.addAttribute("notices", noticeRepository.findAll());
        model.addAttribute("subjects", subjectRepository.findAll());
        return "admin/dashboard";
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
        model.addAttribute("resource", new AcademicResource());
        model.addAttribute("courses", Course.values());
        model.addAttribute("types", ResourceType.values());
        model.addAttribute("subjects", subjectRepository.findByActiveTrueOrderByCourseAscSemesterAscNameAsc());
        return "admin/resource-form";
    }

    @PostMapping("/resources")
    public String saveResource(@ModelAttribute AcademicResource resource) {
        resourceRepository.save(resource);
        return "redirect:/admin";
    }

    @GetMapping("/resources/{id}/edit")
    public String editResource(@PathVariable Long id, Model model) {
        model.addAttribute("resource", resourceRepository.findById(id).orElseThrow());
        model.addAttribute("courses", Course.values());
        model.addAttribute("types", ResourceType.values());
        model.addAttribute("subjects", subjectRepository.findByActiveTrueOrderByCourseAscSemesterAscNameAsc());
        return "admin/resource-form";
    }

    @PostMapping("/resources/{id}/delete")
    public String deleteResource(@PathVariable Long id) {
        resourceRepository.deleteById(id);
        return "redirect:/admin";
    }

    @GetMapping("/notices/new")
    public String newNotice(Model model) {
        model.addAttribute("notice", new Notice());
        return "admin/notice-form";
    }

    @PostMapping("/notices")
    public String saveNotice(@ModelAttribute Notice notice) {
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
}
