package tel_ran.imagga.entities;

public class Tag {
	Double confidence;
	String tag;

	public Tag(Double confidence, String tag) {
		super();
		this.confidence = confidence;
		this.tag = tag;
	}

	public Tag() {
		super();
	}

	public void setConfidence(Double confidence) {
		this.confidence = confidence;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public Double getConfidence() {
		return confidence;
	}

	public String getTag() {
		return tag;
	}

	@Override
	public String toString() {
		return "Tag [confidence=" + confidence + ", tag=" + tag + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((tag == null) ? 0 : tag.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Tag other = (Tag) obj;
		if (tag == null) {
			if (other.tag != null)
				return false;
		} else if (!tag.equals(other.tag))
			return false;
		return true;
	}
	
	

}
