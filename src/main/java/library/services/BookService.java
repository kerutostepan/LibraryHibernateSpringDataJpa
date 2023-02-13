package library.services;

import library.models.Book;
import library.models.Person;
import library.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class BookService {
    private final BookRepository bookRepository;

    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<Book> findAll(boolean sortByYear) {
        if (sortByYear) {
            return bookRepository.findAll(Sort.by("year"));
        } else {
            return bookRepository.findAll();
        }
    }

    public List<Book> findWithPagination(Integer page, Integer booksPerPage, boolean sortByYear) {
        if (sortByYear)
            return bookRepository.findAll(PageRequest.of(page, booksPerPage, Sort.by("year"))).getContent();
        else
            return bookRepository.findAll(PageRequest.of(page, booksPerPage)).getContent();
    }

    public Optional<Book> findOne(int id) {
        return bookRepository.findById(id);
    }

    @Transactional(readOnly = false)
    public void update(int id, Book updatedBook) {
        updatedBook.setId(id);
        bookRepository.save(updatedBook);
    }

    @Transactional(readOnly = false)
    public void save(Book book) {
        bookRepository.save(book);
    }

    @Transactional(readOnly = false)
    public void delete(int id) {
        bookRepository.deleteById(id);
    }

    public Person findOwnerById(int id) {
        return bookRepository.findById(id).map(Book::getOwner).orElse(null);
    }

    public void setPerson(int id, Person person) {
        Optional<Book> book = bookRepository.findById(id);
        book.ifPresent(value -> value.setOwner(person));
        bookRepository.save(book.get());
    }

    public void takeBookBack(int id) {
        Optional<Book> book = bookRepository.findById(id);
        book.get().setOwner(null);
        bookRepository.save(book.get());
    }

    public List<Book> searchByTitle(String text) {
        return bookRepository.findByTitleStartingWith(text);
    }
}
