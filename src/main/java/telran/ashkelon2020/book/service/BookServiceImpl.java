package telran.ashkelon2020.book.service;

import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import telran.ashkelon2020.book.dao.AuthorRepository;
import telran.ashkelon2020.book.dao.BookRepository;
import telran.ashkelon2020.book.dao.PublisherRepository;
import telran.ashkelon2020.book.dto.AuthorDto;
import telran.ashkelon2020.book.dto.BookDto;
import telran.ashkelon2020.book.dto.EntityNotFoundException;
import telran.ashkelon2020.book.model.Author;
import telran.ashkelon2020.book.model.Book;
import telran.ashkelon2020.book.model.Publisher;

@Service
public class BookServiceImpl implements BookService {
	
	@Autowired
	BookRepository bookRepository;
	
	@Autowired
	AuthorRepository authorRepository;
	
	@Autowired
	PublisherRepository publisherRepository;
	
	@Autowired
	ModelMapper modelMapper;

	@Override
	@Transactional(readOnly = true)
	public boolean addBook(BookDto bookDto) {
		if (bookRepository.existsById(bookDto.getIsbn())) {
			return false;
		}
		//Publisher
		String publisherName = bookDto.getPublisher();
		Publisher publisher = publisherRepository.findById(publisherName)
				.orElse(publisherRepository.save(new Publisher(publisherName)));
		//Authors
		Set<Author> authors = bookDto.getAuthors().stream()
				.map(a -> authorRepository.findById(a.getName()).orElse(authorRepository.save(new Author(a.getName(),  a.getBirthDate()))))
				.collect(Collectors.toSet());
		Book book = new Book(bookDto.getIsbn(), bookDto.getTitle(), authors, publisher);
		bookRepository.save(book);
		return true;
	}

	@Override
	public BookDto findBookByIsbn(String isbn) {
		Book book = bookRepository.findById(isbn).orElseThrow(() -> new EntityNotFoundException());
		return modelMapper.map(book, BookDto.class);
	}

	@Override
	@Transactional(readOnly = true)
	public BookDto removeBook(String isbn) {
		Book book = bookRepository.findById(isbn).orElseThrow(() -> new EntityNotFoundException());
		bookRepository.delete(book);
		return modelMapper.map(book, BookDto.class);
	}

	@Override
	@Transactional(readOnly = true)
	public BookDto updateBook(String isbn, String title) {
		Book book = bookRepository.findById(isbn).orElseThrow(() -> new EntityNotFoundException());
		book.setTitle(title);
		return modelMapper.map(book, BookDto.class);
	}
	@Transactional(readOnly = true)
	@Override
	public Iterable<BookDto> findBooksByAuthor(String authorName) {
		Author author = authorRepository.findById(authorName).orElseThrow(() -> new EntityNotFoundException());
		return bookRepository.findBooksByAuthors(author)
				.map(s -> modelMapper.map(s, BookDto.class))
				.collect(Collectors.toSet());
	}
	@Transactional(readOnly = true)
	@Override
	public Iterable<BookDto> findBooksByPublisher(String publisherName) {
		//Publisher publisher = publisherRepository.findById(publisherName).orElseThrow(() -> new EntityNotFoundException());
		return bookRepository.findPublisher(publisherName)
				.map(s -> modelMapper.map(s, BookDto.class))
				.collect(Collectors.toSet());
	}

	@Override
	public Iterable<AuthorDto> findBookAuthors(String isbn) {
		Book book = bookRepository.findById(isbn).orElseThrow(() -> new EntityNotFoundException());
		return book.getAuthors().stream()
				.map(s -> modelMapper.map(s, AuthorDto.class))
				.collect(Collectors.toSet());
	}
	
	@Transactional(readOnly = true)
	@Override
	public Iterable<String> findPublishersByAuthor(String authorName) {
		Author author = authorRepository.findById(authorName).orElseThrow(() -> new EntityNotFoundException());
		return bookRepository.findAllPublishersByAuthors(author)
				.collect(Collectors.toList());
//		return bookRepository.findPublishers(authorName)
//				.collect(Collectors.toList());
	}
	
	@Transactional
	@Override
	public AuthorDto removeAuthor(String authorName) {
		Author author = authorRepository.findById(authorName).orElseThrow(() -> new EntityNotFoundException());
		bookRepository.removeBooksByAuthors(author);
		authorRepository.delete(author);
		return modelMapper.map(author, AuthorDto.class);
	}

}
