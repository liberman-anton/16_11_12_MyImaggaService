package tel_ran.imagga.repo;

import java.util.List;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import tel_ran.imagga.entities.Image;
import tel_ran.imagga.entities.Tag;


public interface ImagesRepository extends CrudRepository<Image, String> {
	
	Iterable<Image> findByTagsTag(String tag);

//	@Query(value="{'image' : ?0}", fields="{'tags' : 1}")
//	@Query(value="{'$elemMatch': {image: ?0}}")
	Image findByImageAndTagsConfidenceGreaterThan(String image, int k);

//	@Query("select t from image.tags t where image.image = :image and t.confidence > :k")
//	List<Tag> findByImageAndTagsConfidenceGreaterThan(@Param("image") String image,
//	                                 @Param("k") int k);
}
