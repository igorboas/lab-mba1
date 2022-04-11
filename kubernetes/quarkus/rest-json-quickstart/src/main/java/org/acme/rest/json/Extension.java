package org.acme.rest.json;

public class Extension {
    public String id;
    public String shortId;
    public String version;
    public String name;
    public String description;
    public String shortName;
    public String category;
    public String providesExampleCode;
    public String providesCode;
    public String guide;
    public String order;
    public String platform;
    public String bom;

    public Extension() {
    }

    public Extension(String id, String shortId, String version, String name, String description, String shortName, String category, String providesExampleCode, String providesCode, String guide, String order, String platform, String bom ) {
	this.id = id;
	this.shortId = shortId;
	this.version = version;
	this.name = name;
	this.description = description;
	this.shortName = shortName;
	this.category = category;
	this.providesExampleCode = providesExampleCode;
	this.providesCode = providesCode;
	this.guide = guide;
	this.order = order;
	this.platform = platform;
	this.bom = bom;
    }
}
