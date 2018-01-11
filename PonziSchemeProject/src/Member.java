
public class Member {

	private String name;
	private int assets;
	private String sponsor;
	
	public Member(String name, int value, String sponsor) {
		this.name = name;
		this.assets = value;
		this.sponsor = sponsor;
	}
	public Member() {
		this.name = null;
		this.assets = 0;
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
		return assets;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(int value) {
		this.assets = value;
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
	

	@Override
	public String toString() {
		return "Member [name=" + name + ", value=" + assets + ", sponsor=" + sponsor + "]";
	}
	
	
}
