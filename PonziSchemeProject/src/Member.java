
public class Member {

	private String name;
	private int value;
	private String sponsor;
	
	public Member(String name, int value, String sponsor) {
		this.name = name;
		this.value = value;
		this.sponsor = sponsor;
	}
	public Member() {
		this.name = null;
		this.value = 0;
		this.sponsor = null;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the value
	 */
	public int getValue() {
		return value;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(int value) {
		this.value = value;
	}
	/**
	 * @return the sponsor
	 */
	public String getSponsor() {
		return sponsor;
	}
	/**
	 * @param sponsor the sponsor to set
	 */
	public void setSponsor(String sponsor) {
		this.sponsor = sponsor;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Member [name=" + name + ", value=" + value + ", sponsor=" + sponsor + "]";
	}
	
	
}
