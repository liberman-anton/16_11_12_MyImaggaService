package tel_ran.imagga.controller;

import java.io.FileNotFoundException;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import tel_ran.imagga.entities.Image;
import tel_ran.imagga.entities.Tag;
import tel_ran.imagga.model.dao.ImagesMongoDB;

@RestController
@SpringBootApplication
@ImportResource("classpath:beans.xml")
public class MyImaggaService {
	static final String AUTH_TOKEN = "Basic YWNjXzYwMjM2N2E3YTZiNzVmZTo2ZDAyMjQyODRjZmVjNDZlZjA4MmY5OGE5NTA5ZTdjMw==";
	static final String URL_SERVICE = "http://api.imagga.com/v1/tagging?url=";
	static final String CONSTANTA_K = "20";
	static final String CONSTANTA_M = "65";
	static final String CONSTANTA_L = "65";
	static int lll = 65;
	static RestTemplate restTemplate = new RestTemplate();
	@Autowired
	ImagesMongoDB imagesMongo;

	public static void setLll(int lll) {
		MyImaggaService.lll = lll;
	}

	@RequestMapping(value = "get_images_query", method = RequestMethod.POST)
	public List<Image> getImagesByTagsQuery(@RequestBody String[] tags,
			@RequestParam(defaultValue = CONSTANTA_L) int l) {
		List<Image> res = new ArrayList<>();
		HashMap<Image, Integer> mapImages = getMapImagesByTags(tags);
		if (!mapImages.isEmpty()) {
			for (Image image : mapImages.keySet()) {
				if (mapImages.get(image) >= tags.length * lll / 100)
					res.add(image);
			}
		}
		return res;
	}

	private HashMap<Image, Integer> getMapImagesByTags(String[] tags) {
		HashMap<Image, Integer> res = new HashMap<>();
		List<Image> listImages;
		for(int i = 0; i < tags.length; i++){
			listImages = imagesMongo.findImagesByTag(tags[i]);
			if(!listImages.isEmpty())
				putImagesToMap(listImages, res);
		}
		return res;
	}

	private void putImagesToMap(List<Image> listImages, HashMap<Image, Integer> res) {
		Integer counter = null;
		for(Image image : listImages){
			counter = res.get(image);
			if(counter == null)
				counter = 0;
			res.put(image, ++counter);
		}
	}

	@RequestMapping(value = "get_images", method = RequestMethod.POST)
	public List<Image> getImagesByTags(@RequestBody String[] tags, 
			@RequestParam(defaultValue = CONSTANTA_L) int l) {
		List<Tag> listOfTags = getListOfTags(tags);
		List<Image> res = new ArrayList<>();
		Iterable<Image> allImages = imagesMongo.findAll();
		for (Image image : allImages) {
			if (counterSameTags(listOfTags, image.getTags()) > tags.length * l / 100)
				res.add(image);
		}
		return res;
	}

	private List<Tag> getListOfTags(String[] tags) {
		List<Tag> res = new ArrayList<>();
		for (String tag : tags) {
			res.add(new Tag(0D, tag));
		}
		return res;
	}

	@RequestMapping(value = "get_similar")
	public List<String> getSimilar(String url, @RequestParam(defaultValue = CONSTANTA_M) int m)
			throws FileNotFoundException {
		List<String> res = new ArrayList<>();
		List<Tag> tags = getTags(url, 0);
		Iterable<Image> allImages = imagesMongo.findAll();
		for (Image image : allImages) {
			String url2 = image.getImage();
			if (counterSameTags(tags, image.getTags()) >= tags.size() * m / 100 && !url2.equals(url))
				res.add(url2);
		}
		return res;
	}

	private int counterSameTags(List<Tag> tags, List<Tag> tags2) {
		int res = 0;
		for (Tag tag : tags)
			if (tags2.contains(tag))
				res++;
		return res;
	}

	@RequestMapping(value = "add")
	public boolean add(String url) {
		Image image = null;
		image = getImageFromDB(url);
		if (image != null)
			return false;
		image = getImageFromImagga(url);
		if (image == null)
			return false;
		imagesMongo.addImage(image);
		return true;
	}

	@RequestMapping(value = "get_tags_mongo")
	public List<Tag> getTagsMongo(String url,
			@RequestParam(defaultValue = CONSTANTA_K) int k){
		return imagesMongo.getTagsbyUrlGreaterK(url,20);
	}
	
	@RequestMapping(value = "get_tags")
	public List<Tag> getTags(String url, 
			@RequestParam(defaultValue = CONSTANTA_K) int k) throws FileNotFoundException {
		Image image = null;
		image = getImageFromDB(url);
		if (image == null) {
			image = getImageFromImagga(url);
			if (image == null)
				throw new FileNotFoundException();
			imagesMongo.addImage(image);
		}
		return getListTagsGreaterK(image.getTags(), k);
	}

	private List<Tag> getListTagsGreaterK(List<Tag> tags, int k) {
		ArrayList<Tag> res = new ArrayList<>();
		for (Tag tag : tags) {
			Double confidence = tag.getConfidence();
			if (confidence < k)
				break;
			res.add(tag);
		}
		return res;
	}

	private Image getImageFromDB(String url) {
		return imagesMongo.findImageByUrl(url);
	}

	private Image getImageFromImagga(String url) {
		Image image = null;
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", AUTH_TOKEN);
		HttpEntity<String> request = new HttpEntity<>(headers);
		ResponseEntity<Map<String, List<Image>>> response = restTemplate.exchange(URL_SERVICE + url, HttpMethod.GET,
				request, new ParameterizedTypeReference<Map<String, List<Image>>>() {
				});
		Map<String, List<Image>> map = response.getBody();
		if (!map.isEmpty() && map.containsKey("results") && !map.get("results").isEmpty())
			image = map.get("results").get(0);
		return image;
	}

	public static void main(String[] args) {
		AbstractApplicationContext ctx = new FileSystemXmlApplicationContext("mbeansNoReg.xml");
		SpringApplication.run(MyImaggaService.class, args);
		
	}
	public MyImaggaService(){}
}
