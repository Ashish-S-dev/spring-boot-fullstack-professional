package com.example.demo.site;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(
        name = "sites",
        indexes = {
                @Index(name = "idx_site_active", columnList = "active")
        }
)
@JsonIgnoreProperties({
    "hibernateLazyInitializer",
    "handler"
})
public class Site {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 150)
    private String siteName;

    @NotBlank
    @Column(nullable = false, length = 255)
    private String location;

    @Column(nullable = false)
    private boolean active = true;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	public String toString() {
		return "Site [id=" + id + ", siteName=" + siteName + ", location=" + location + ", active=" + active + "]";
	}

	public Site(@NotBlank String siteName, @NotBlank String location, boolean active) {
		super();
		this.siteName = siteName;
		this.location = location;
		this.active = active;
	}
    
    public Site() {}
    
    
}