package library.controllers;


import library.models.Book;
import library.models.Person;
import library.services.BookService;
import library.services.PeopleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/books")
public class BookController {
    private final BookService bookService;
    private final PeopleService peopleService;

    @Autowired
    public BookController(BookService bookService, PeopleService peopleService) {
        this.bookService = bookService;
        this.peopleService = peopleService;
    }

    @GetMapping()
    public String index(Model model, @RequestParam(value = "page", required = false) Integer page,
                        @RequestParam(value = "books_per_page", required = false) Integer booksPerPage,
                        @RequestParam(value = "sort_by_year", required = false) boolean sortByYear) {
        if (page == null || booksPerPage == null) {
            model.addAttribute("books", bookService.findAll(sortByYear));
        } else {
            model.addAttribute("books", bookService.findWithPagination(page, booksPerPage, sortByYear));
        }
        return "book/index";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable("id") int id, Model model, @ModelAttribute("person") Person person) {
        model.addAttribute("books", bookService.findOne(id));

        Person bookOwner = bookService.findOwnerById(id);

        if (bookOwner != null)
            model.addAttribute("owner", bookOwner);
        else
            model.addAttribute("people", peopleService.findAll());
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
        bookService.save(books);
        return "redirect:/books";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable("id") int id, Model model) {
        model.addAttribute("books", bookService.findOne(id));
        return "book/edit";
    }

    @PatchMapping("/{id}")
    public String update(@PathVariable("id") int id, @ModelAttribute("books") @Valid Book books, BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return "book/edit";
        bookService.update(id, books);
        return "redirect:/books";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") int id) {
        bookService.delete(id);
        return "redirect:/books";
    }

    @PatchMapping("/{id}/setPerson")
    public String setPerson(@PathVariable("id") int id, @ModelAttribute("person") Person person) {
        bookService.setPerson(id, person);
        return "redirect:/books/" + id;
    }

    @PatchMapping("/{id}/takeBookBack")
    public String takeBookBack(@PathVariable("id") int id) {
        bookService.takeBookBack(id);
        return "redirect:/books/" + id;
    }

    @GetMapping("/search")
    public String searchPage() {
        return "book/search";
    }

    @PostMapping("/search")
    public String searchBook(Model model, @RequestParam("text") String text) {
        model.addAttribute("books", bookService.searchByTitle(text));
        return "book/search";
    }
}
