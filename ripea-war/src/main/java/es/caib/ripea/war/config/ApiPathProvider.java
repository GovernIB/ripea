package es.caib.ripea.war.config;

import com.mangofactory.swagger.paths.SwaggerPathProvider;
 
public class ApiPathProvider 
extends SwaggerPathProvider 
{
	
	private SwaggerPathProvider defaultSwaggerPathProvider;

	@Override
	public String getApiResourcePrefix() {
		return defaultSwaggerPathProvider.getApiResourcePrefix();
	}

	public void setDefaultSwaggerPathProvider(SwaggerPathProvider defaultSwaggerPathProvider) {
		this.defaultSwaggerPathProvider = defaultSwaggerPathProvider;
	}

	@Override
	protected String applicationPath() {
//        return UriComponentsBuilder
//        		.fromPath("/")
//                .fromHttpUrl(basePath)
//                .path(servletContext.getContextPath())
//                .build()
//                .toString();
		return "/";
	}
 
	@Override
	protected String getDocumentationPath() {
//        return UriComponentsBuilder
//                .fromHttpUrl(applicationPath())
//                .pathSegment("api-docs/")
//                .build()
//                .toString();
		return "/api-docs/";
	}

}
