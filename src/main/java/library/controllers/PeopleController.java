package library.controllers;

import library.dao.PersonDAO;
import library.models.Person;
import library.util.PersonValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/people")
public class PeopleController {
    private final PersonValidator personValidator;
    private final PersonDAO personDAO;

    @Autowired
    public PeopleController(PersonValidator personValidator, PersonDAO personDAO) {
        this.personValidator = personValidator;
        this.personDAO = personDAO;
    }

    @GetMapping()
    public String index(Model model) {
        model.addAttribute("people", personDAO.index());
        return "person/index";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable("id") int id, Model model) {
        model.addAttribute("people", personDAO.show(id));
        model.addAttribute("books", personDAO.findPersonById(id));
        return "/person/show";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable("id") int id, Model model) {
        model.addAttribute("people", personDAO.show(id));
        return "person/edit";
    }

    @PatchMapping("/{id}")
    public String update(@PathVariable("id") int id, @ModelAttribute("people") @Valid Person person, BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return "person/edit";
        personDAO.edit(id, person);
        return "redirect:/people";
    }

    @GetMapping("/new")
    public String createPerson(Model model) {
        model.addAttribute("people", new Person());
        return "person/new";
    }

    @PostMapping
    public String create(@ModelAttribute("people") @Valid Person person, BindingResult bindingResult) {
        personValidator.validate(person,bindingResult);
        if (bindingResult.hasErrors())
            return "person/new";
        personDAO.save(person);
        return "redirect:/people";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") int id) {
        personDAO.delete(id);
        return "redirect:/people";
    }

}