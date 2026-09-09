package in.bbabca.wallah.controller;

import in.bbabca.wallah.model.AcademicResource;
import in.bbabca.wallah.model.Opportunity;
import in.bbabca.wallah.repository.AcademicResourceRepository;
import in.bbabca.wallah.repository.OpportunityRepository;
import in.bbabca.wallah.repository.SubjectRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SeoDetailController {

    private final AcademicResourceRepository resourceRepository;
    private final OpportunityRepository opportunityRepository;
    private final SubjectRepository subjectRepository;

    @Value("${app.site.url:http://localhost:8080}")
    private String siteUrl;

    public SeoDetailController(AcademicResourceRepository resourceRepository,
                               OpportunityRepository opportunityRepository,
                               SubjectRepository subjectRepository) {
        this.resourceRepository = resourceRepository;
        this.opportunityRepository = opportunityRepository;
        this.subjectRepository = subjectRepository;
    }

    @GetMapping("/resource/{id}")
    public String resourceDetail(@PathVariable Long id, Model model) {
        AcademicResource resource = resourceRepository.findById(id).orElseThrow();
        if (!resource.isActive()) {
            throw new IllegalArgumentException("Resource is not available");
        }

        String description = metaDescription(resource.getDescription(),
                resource.getTitle() + " for " + resource.getCourse() + " Semester " + resource.getSemester() + " on BBA/BCA Wallah JUT.");
        String canonicalUrl = cleanSiteUrl() + "/resource/" + id;

        model.addAttribute("resource", resource);
        model.addAttribute("seoTitle", resource.getTitle() + " | BBA/BCA Wallah JUT");
        model.addAttribute("seoDescription", description);
        model.addAttribute("canonicalUrl", canonicalUrl);
        model.addAttribute("shareText", resource.getTitle() + " - BBA/BCA Wallah JUT");
        return "resource-detail";
    }

    @GetMapping("/opportunity/{id}")
    public String opportunityDetail(@PathVariable Long id, Model model) {
        Opportunity opportunity = opportunityRepository.findById(id).orElseThrow();
        if (!opportunity.isActive()) {
            throw new IllegalArgumentException("Opportunity is not available");
        }

        String fallback = opportunity.getRole() + " at " + opportunity.getCompany() + " - " + opportunity.getType() + " opportunity for BBA/BCA students.";
        String description = metaDescription(opportunity.getDescription(), fallback);
        String canonicalUrl = cleanSiteUrl() + "/opportunity/" + id;

        model.addAttribute("opportunity", opportunity);
        model.addAttribute("seoTitle", opportunity.getRole() + " at " + opportunity.getCompany() + " | BBA/BCA Wallah JUT");
        model.addAttribute("seoDescription", description);
        model.addAttribute("canonicalUrl", canonicalUrl);
        model.addAttribute("shareText", opportunity.getRole() + " at " + opportunity.getCompany());
        return "opportunity-detail";
    }

    @GetMapping(value = "/sitemap.xml", produces = "application/xml")
    @ResponseBody
    public String sitemap() {
        String base = cleanSiteUrl();
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        xml.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">");

        addUrl(xml, base + "/");
        addUrl(xml, base + "/resources");
        addUrl(xml, base + "/notices");
        addUrl(xml, base + "/placements");
        addUrl(xml, base + "/internships");
        addUrl(xml, base + "/opportunities");

        subjectRepository.findAll().stream()
                .filter(subject -> subject.isActive())
                .forEach(subject -> addUrl(xml, base + "/subject/" + subject.getId()));

        resourceRepository.findByActiveTrueOrderByFeaturedDescCreatedAtDesc()
                .forEach(resource -> addUrl(xml, base + "/resource/" + resource.getId()));

        opportunityRepository.findByActiveTrueOrderByFeaturedDescDeadlineAscCreatedAtDesc()
                .forEach(opportunity -> addUrl(xml, base + "/opportunity/" + opportunity.getId()));

        xml.append("</urlset>");
        return xml.toString();
    }

    private void addUrl(StringBuilder xml, String url) {
        xml.append("<url><loc>").append(xmlEscape(url)).append("</loc></url>");
    }

    private String cleanSiteUrl() {
        return siteUrl == null ? "http://localhost:8080" : siteUrl.replaceAll("/+$", "");
    }

    private String metaDescription(String value, String fallback) {
        String text = value == null || value.isBlank() ? fallback : value.trim();
        text = text.replaceAll("\\s+", " ");
        return text.length() <= 160 ? text : text.substring(0, 157) + "...";
    }

    private String xmlEscape(String value) {
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}
