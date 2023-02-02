package library.controllers;

import library.dao.BookDAO;
import library.dao.PersonDAO;
import library.models.Book;
import library.models.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@Controller
@RequestMapping("/books")
public class BookController {
    private final BookDAO bookDAO;
    private final PersonDAO personDAO;

    @Autowired
    public BookController(BookDAO bookDAO, PersonDAO personDAO) {
        this.bookDAO = bookDAO;
        this.personDAO = personDAO;
    }

    @GetMapping()
    public String index(Model model) {
        model.addAttribute("books", bookDAO.index());
        return "book/index";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable("id") int id, Model model, @ModelAttribute("person") Person person) {
        model.addAttribute("books", bookDAO.show(id));

        Optional<Person> bookOwner = bookDAO.getBookOwner(id);

        if (bookOwner.isPresent())
            model.addAttribute("owner", bookOwner.get());
        else
            model.addAttribute("people", personDAO.index());
        return "book/show";
    }

    @GetMapping("/new")
    public String newBook(Model model) {
        model.addAttribute("books", new Book());
        return "book/new";
    }

    @PostMapping
    public String create(@ModelAttribute("books") @Valid Book books, BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return "book/new";
        bookDAO.save(books);
        return "redirect:/books";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable("id") int id, Model model) {
        model.addAttribute("books", bookDAO.show(id));
        return "book/edit";
    }

    @PatchMapping("/{id}")
    public String update(@PathVariable("id") int id, @ModelAttribute("books") @Valid Book books, BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return "book/edit";
        bookDAO.edit(id, books);
        return "redirect:/books";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") int id) {
        bookDAO.delete(id);
        return "redirect:/books";
    }

    @PatchMapping("/{id}/setPerson")
    public String setPerson(@PathVariable("id") int id, @ModelAttribute("person") Person person) {
        bookDAO.setPerson(id, person);
        return "redirect:/books/" + id;
    }

    @PatchMapping("/{id}/takeBookBack")
    public String takeBookBack(@PathVariable("id") int id) {
        bookDAO.takeBackBook(id);
        return "redirect:/books/" + id;
    }
}
