package es.caib.ripea.plugin;


import java.util.Properties;

public abstract class RipeaAbstractPluginProperties {
	
	private final String propertyKeyBase;
	private final Properties properties;

	public RipeaAbstractPluginProperties() {
		this("", (Properties) null);
	}

	public RipeaAbstractPluginProperties(String propertyKeyBase) {
		this(propertyKeyBase, (Properties) null);
	}

	public RipeaAbstractPluginProperties(String propertyKeyBase, Properties properties) {
		this.propertyKeyBase = propertyKeyBase;
		this.properties = properties;
	}

	public final String getProperty(String partialName) {
		return this.getProperty(partialName, (String) null);
	}

	public final String getProperty(String partialName, String defaultValue) {
		String fullName = this.getPropertyName(partialName);
		return this.properties == null
				? System.getProperty(fullName, defaultValue)
				: this.properties.getProperty(fullName, defaultValue);
	}

	public final String getPropertyName(String partialName) {
		return this.propertyKeyBase + partialName;
	}

	public final String getPropertyRequired(String partialName) throws Exception {
		String value = this.getProperty(partialName);
		if (value == null) {
			throw new Exception("Property " + this.getPropertyName(partialName)
					+ " is required but it has not defined in the Properties");
		} else {
			return value;
		}
	}
	
	public boolean getAsBoolean(String key) {
		return new Boolean(getProperty(key)).booleanValue();
	}
	public int getAsInt(String key) {
		return new Integer(getProperty(key)).intValue();
	}
	public long getAsLong(String key) {
		return new Long(getProperty(key)).longValue();
	}
	public float getAsFloat(String key) {
		return new Float(getProperty(key)).floatValue();
	}
	public double getAsDouble(String key) {
		return new Double(getProperty(key)).doubleValue();
	}

	public final Properties getPluginProperties() {
		return this.properties;
	}

	public final String getPropertyKeyBase() {
		return this.propertyKeyBase;
	}
}