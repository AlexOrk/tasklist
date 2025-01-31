package orkhoian.aleksei.tasklist.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import orkhoian.aleksei.tasklist.service.NulabService;
import orkhoian.aleksei.tasklist.service.UserService;
import orkhoian.aleksei.tasklist.utils.Utils;
import orkhoian.aleksei.tasklist.dto.nulab.IssueAddParamsDto;
import orkhoian.aleksei.tasklist.dto.nulab.IssueDto;
import orkhoian.aleksei.tasklist.dto.nulab.IssueGetParamsDto;
import orkhoian.aleksei.tasklist.dto.nulab.ProjectDto;

import java.util.List;

@RestController
@RequestMapping("/api/v1/nulab")
@Tag(name = "Nulab controller", description = "Nulab Backlog API")
@Slf4j
public class NulabController {

    private final NulabService nulabService;
    private final UserService userService;

    @Autowired
    public NulabController(NulabService nulabService, UserService userService) {
        this.nulabService = nulabService;
        this.userService = userService;
    }

    @GetMapping("/projects")
    public List<ProjectDto> getProjectList() {
        return nulabService.getProjectList(Utils.getCurrentUserApiKey(userService));
    }

    @GetMapping("/issues")
    public List<IssueDto> getIssueList(IssueGetParamsDto params) {
        return nulabService.getIssueList(Utils.getCurrentUserApiKey(userService), params);
    }

    @GetMapping("/issues/{issueIdOrKey}")
    public IssueDto getIssue(@PathVariable String issueIdOrKey) {
        return nulabService.getIssue(Utils.getCurrentUserApiKey(userService), issueIdOrKey);
    }

    @PostMapping("/issues")
    public IssueDto addIssue(@Valid IssueAddParamsDto params) {
        return nulabService.addIssue(Utils.getCurrentUserApiKey(userService), params);
    }
}
