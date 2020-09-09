package telran.ashkelon2020.book.dao;

import java.util.stream.Stream;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import telran.ashkelon2020.book.model.Author;
import telran.ashkelon2020.book.model.Book;

public interface BookRepository extends JpaRepository<Book, String> {
	Stream<Book> findBooksByAuthors(Author author);
//	@Query("select b from Book b  where b.authors. =?1")
//	Stream<Book> findBooks(String name);
	@Query("select b from Book b where b.publisher.publisherName=?1")
	Stream<Book> findPublisher(String publisherName);
	//Stream<Book> findBooksByPublisher(Publisher publisher);
//	@Query("select p.publisherName, a.name from Book b, Publisher p, Author a where a.name =?1 group by p.publisherName")
//	Stream<String> findPublishers(String authorName);
	Stream<String> findAllPublishersByAuthors(Author author);
	void removeBooksByAuthors(Author author);
}
