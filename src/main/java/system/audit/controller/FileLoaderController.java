package system.audit.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import system.audit.service.FileLoaderService;



/**
 *
 * Класс, предоставляющей функционал для скачивания и удаления файла по id.
 */
@Controller
@AllArgsConstructor
@RequestMapping("/files")
public class FileLoaderController {

    private final FileLoaderService fileLoaderService;

    @GetMapping("/audit/{id}")
    public void loadAuditDocumentFileById(HttpServletResponse response, @PathVariable Integer id) {
        fileLoaderService.loadAuditDocumentFileById(response, id);
    }

    @GetMapping("/finding/{id}")
    public void loadFindingDocumentFileById(HttpServletResponse response, @PathVariable Integer id) {
        fileLoaderService.loadFindingDocumentFileById(response, id);
    }

    @GetMapping("/audit/delete/{id}")
    public ModelAndView deleteAuditDocumentFileById(HttpServletRequest request, @PathVariable Integer id) {
        fileLoaderService.deleteAuditDocumentFileById(id);
        final var referer = "redirect:" + request.getHeader("Referer");
        return new ModelAndView(referer);
    }

    @GetMapping("/finding/delete/{id}")
    public ModelAndView deleteFindingDocumentFileById(HttpServletRequest request, @PathVariable Integer id) {
        fileLoaderService.deleteFindingDocumentFileById(id);
        final var referer = "redirect:" + request.getHeader("Referer");
        return new ModelAndView(referer);
    }

}
