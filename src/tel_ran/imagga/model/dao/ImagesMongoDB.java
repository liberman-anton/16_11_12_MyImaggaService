package tel_ran.imagga.model.dao;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;

import tel_ran.imagga.entities.Image;
import tel_ran.imagga.entities.Tag;
import tel_ran.imagga.repo.ImagesRepository;

public class ImagesMongoDB {
	@Autowired
	ImagesRepository imagesMongo;
		
	public boolean addImages(Iterable<Image> images){
		if(images == null || !images.iterator().hasNext())
			return false;
		imagesMongo.save(images);
		return true;
	}

	public Image findImageByUrl(String url) {
		return imagesMongo.findOne(url);
	}

	public boolean addImage(Image image) {
		if(image == null)
			return false;
		imagesMongo.save(image);
		return true;
	}

	public Iterable<Image> findAll() {
		return imagesMongo.findAll();
	}

	public List<Image> findImagesByTag(String tag) {
		Iterable<Image> images = imagesMongo.findByTagsTag(tag);
		List<Image> res = new ArrayList<>();
		if(images != null && images.iterator().hasNext())
			for(Image image : images)
				res.add(image);
		return res;
	}

	public List<Tag> getTagsbyUrlGreaterK(String url, int k) {
		List<Tag> tags= imagesMongo.findByImageAndTagsConfidenceGreaterThan(url,k).getTags();
		List<Tag> res = new ArrayList<>();
		if(tags != null && tags.iterator().hasNext())
			for(Tag tag : tags)
				res.add(tag);
		return res;
	}

}
