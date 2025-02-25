package es.caib.ripea.back.helper;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;

import es.caib.ripea.back.helper.AjaxHelper.AjaxFormResponse;
import es.caib.ripea.service.intf.dto.PaginaDto;
import es.caib.ripea.service.intf.dto.PaginacioParamsDto;
import es.caib.ripea.service.intf.dto.PaginacioParamsDto.OrdreDireccioDto;
import lombok.Getter;
import lombok.Setter;

/**
 * Mètodes d'ajuda per a gestionar datatables.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DatatablesHelper {

	private static final String ATRIBUT_ID = "DT_Id";
	private static final String ATRIBUT_ROW_ID = "DT_RowId";
	private static final String ATRIBUT_ROW_SELECTED = "DT_RowSelected";

	public static PaginacioParamsDto getPaginacioDtoFromRequest(
			HttpServletRequest request) {
		return getPaginacioDtoFromRequest(request, null, null);
	}
	private static PaginacioParamsDto getPaginacioDtoFromRequest(
			HttpServletRequest request,
			Map<String, String[]> mapeigFiltres,
			Map<String, String[]> mapeigOrdenacions) {
		DatatablesParams params = new DatatablesParams(request);
		LOGGER.debug("Informació de la pàgina obtingudes de datatables (" +
				"draw=" + params.getDraw() + ", " +
				"start=" + params.getStart() + ", " +
				"length=" + params.getLength() + ")");
		PaginacioParamsDto paginacio = new PaginacioParamsDto();
		int paginaNum = params.getStart() / params.getLength();
		paginacio.setPaginaNum(paginaNum);
		if (params.getLength() != null && params.getLength().intValue() == -1) {
			paginacio.setPaginaTamany(Integer.MAX_VALUE);
		} else {
			paginacio.setPaginaTamany(params.getLength());
		}
		paginacio.setFiltre(params.getSearchValue());
		for (int i = 0; i < params.getColumnsSearchValue().size(); i++) {
			String columna = params.getColumnsData().get(i);
			String[] columnes = new String[] {columna};
			if (mapeigFiltres != null && mapeigFiltres.get(columna) != null) {
				columnes = mapeigFiltres.get(columna);
			}
			for (String col: columnes) {
				if (!"<null>".equals(col)) {
					paginacio.afegirFiltre(
							col,
							params.getColumnsSearchValue().get(i));
					LOGGER.debug("Afegit filtre a la paginació (" +
							"columna=" + col + ", " +
							"valor=" + params.getColumnsSearchValue().get(i) + ")");
				}
			}
		}
		for (int i = 0; i < params.getOrderColumn().size(); i++) {
			int columnIndex = params.getOrderColumn().get(i);
			String columna = params.getColumnsData().get(columnIndex);
			OrdreDireccioDto direccio;
			if ("asc".equals(params.getOrderDir().get(i)))
				direccio = OrdreDireccioDto.ASCENDENT;
			else
				direccio = OrdreDireccioDto.DESCENDENT;
			String[] columnes = new String[] {columna};
			if (mapeigOrdenacions != null && mapeigOrdenacions.get(columna) != null) {
				columnes = mapeigOrdenacions.get(columna);
			}
			for (String col: columnes) {
				paginacio.afegirOrdre(col, direccio);
				LOGGER.debug("Afegida ordenació a la paginació (columna=" + columna + ", direccio=" + direccio + ")");
			}
		}
		LOGGER.debug("Informació de la pàgina sol·licitada (paginaNum=" + paginacio.getPaginaNum() + ", paginaTamany=" + paginacio.getPaginaTamany() + ")");
		return paginacio;
	}

	public static <T> DatatablesResponse getDatatableResponse(
			HttpServletRequest request,
			PaginaDto<T> pagina) {
		return getDatatableResponse(request, null, pagina, null, null);
	}
	public static <T> DatatablesResponse getDatatableResponse(
			HttpServletRequest request,
			PaginaDto<T> pagina,
			String atributId) {
		return getDatatableResponse(request, null, pagina, atributId, null);
	}
	public static <T> DatatablesResponse getDatatableResponse(
			HttpServletRequest request,
			PaginaDto<T> pagina,
			String atributId,
			String atributSeleccio) {
		return getDatatableResponse(request, null, pagina, atributId, atributSeleccio);
	}
	public static <T> DatatablesResponse getDatatableResponse(
			HttpServletRequest request,
			BindingResult bindingResult,
			PaginaDto<T> pagina) {
		return getDatatableResponse(request, bindingResult, pagina, null, null);
	}
	@SuppressWarnings("unchecked")
	public static <T> DatatablesResponse getDatatableResponse(
			HttpServletRequest request,
			BindingResult bindingResult,
			PaginaDto<T> pagina,
			String atributId,
			String atributSeleccio) {
		LOGGER.debug("Generant informació de resposta per datatable (" +
				"numero=" + pagina.getNumero() + ", " +
				"tamany=" + pagina.getTamany() + ", " +
				"total=" + pagina.getTotal() + ", " +
				"elementsTotal=" + pagina.getElementsTotal() + ")");
		if (bindingResult != null && bindingResult.hasErrors()) {
			DatatablesResponse emptyResponse = getDatatableResponse(request, null, (List<T>)null, null);
			emptyResponse.setFiltreFormResponse(
					AjaxHelper.generarAjaxFormErrors(null, bindingResult));
			return emptyResponse;
		}
		DatatablesParams params = new DatatablesParams(request);
		DatatablesResponse response = new DatatablesResponse();
		response.setDraw((params.getDraw() != null) ? params.getDraw().intValue() : 0);
		response.setRecordsFiltered(pagina.getElementsTotal());
		response.setRecordsTotal(pagina.getElementsTotal());
		List<Map<String, Object>> dataMap = new ArrayList<Map<String, Object>>();
		Collection<? extends Object> seleccio = null;
		if (atributSeleccio != null) {
			seleccio = (Collection<? extends Object>)request.getSession().getAttribute(atributSeleccio);
		}
		if (pagina.getContingut() != null) {
			long index = 0;
			for (T registre: pagina.getContingut()) {
				List<PropertyDescriptor> descriptors = getBeanPropertyDescriptors(registre);
				Object[] dadesRegistre = new Object[params.getColumnsData().size()];
				Map<String, Object> mapRegistre = new HashMap<String, Object>();
				for (int i = 0; i < params.getColumnsData().size(); i++) {
					String propietatNom = params.getColumnsData().get(i);
					try {
						if (propietatNom.contains(".")) {
							propietatNom = propietatNom.substring(0, propietatNom.indexOf("."));
						}
						Object valor = getPropietatValor(registre, propietatNom, descriptors);
						mapRegistre.put(
								propietatNom,
								valor);
						dadesRegistre[i] = valor;
					} catch (Exception ex) {
						dadesRegistre[i] = "(!)";
						LOGGER.error(
								"No s'ha pogut llegir la propietat de l'objecte (" +
								"propietatNom=" + propietatNom + ")",
								ex);
					}
				}
				Object valorId = null;
				if (atributId != null) {
					try {
						valorId = getPropietatValor(registre, atributId, descriptors);
					} catch (Exception ex) {
						LOGGER.error(
								"No s'ha pogut llegir la propietat de l'objecte (" +
										"propietatNom=" + atributId + ")",
								ex);
					}
				} else {
					valorId = new Long(index);
				}
				mapRegistre.put(
						ATRIBUT_ROW_ID,
						"row_" + valorId);
				mapRegistre.put(
						ATRIBUT_ID,
						valorId);
				mapRegistre.put(
						ATRIBUT_ROW_SELECTED,
						(seleccio != null && seleccio.contains(valorId)));
				dataMap.add(mapRegistre);
				index++;
			}
		}
		response.setData(dataMap);
		LOGGER.debug("Informació per a datatables (" +
				"draw=" + response.getDraw() + "," +
				"recordsFiltered=" + response.getRecordsFiltered() + "," +
				"recordsTotal=" + response.getRecordsTotal() + ")");
		return response;
	}
	public static <T> DatatablesResponse getDatatableResponse(
			HttpServletRequest request,
			List<T> llista) {
		return getDatatableResponse(request, null, llista, null);
	}
	public static <T> DatatablesResponse getDatatableResponse(
			HttpServletRequest request,
			List<T> llista,
			String atributId) {
		return getDatatableResponse(request, null, llista, atributId);
	}
	public static <T> DatatablesResponse getDatatableResponse(
			HttpServletRequest request,
			BindingResult bindingResult,
			List<T> llista) {
		return getDatatableResponse(request, bindingResult, llista, null);
	}
	public static <T> DatatablesResponse getDatatableResponse(
			HttpServletRequest request,
			BindingResult bindingResult,
			List<T> llista,
			String atributId) {
		if (llista != null)
			LOGGER.debug("Informació de la llista (tamany=" + llista.size() + ")");
		else
			LOGGER.debug("Informació de la llista (null)");
		PaginaDto<T> dto = new PaginaDto<T>();
		dto.setNumero(0);
		dto.setTamany((llista != null) ? llista.size() : 0);
		dto.setTotal(1);
		dto.setElementsTotal((llista != null) ? llista.size() : 0);
		dto.setAnteriors(false);
		dto.setPrimera(true);
		dto.setPosteriors(false);
		dto.setDarrera(true);
		dto.setContingut(llista);
		return getDatatableResponse(request, bindingResult, dto, atributId, null);
	}

	/*public static <T> DatatablesResponse getDatatableResponse(
			HttpServletRequest request,
			T element) {
		return getDatatableResponse(request, null, element, null);
	}
	public static <T> DatatablesResponse getDatatableResponse(
			HttpServletRequest request,
			T element,
			String atributId) {
		return getDatatableResponse(request, null, element, atributId);
	}
	public static <T> DatatablesResponse getDatatableResponse(
			HttpServletRequest request,
			BindingResult bindingResult,
			T element) {
		return getDatatableResponse(request, bindingResult, element, null);
	}
	public static <T> DatatablesResponse getDatatableResponse(
			HttpServletRequest request,
			BindingResult bindingResult,
			T element,
			String atributId) {
		List<T> list = new ArrayList<T>();
		list.add(element);
		return getDatatableResponse(request, bindingResult, list, atributId);
	}*/

	public static <T> DatatablesResponse getEmptyDatatableResponse(
			HttpServletRequest request) {
		return getDatatableResponse(request, null, (List<T>)null, null);
	}
	public static <T> DatatablesResponse getEmptyDatatableResponse(
			HttpServletRequest request,
			BindingResult bindingResult) {
		return getDatatableResponse(request, bindingResult, (List<T>)null, null);
	}



	public static class DatatablesResponse {
		private int draw;
		private long recordsTotal;
		private long recordsFiltered;
		private List<Map<String, Object>> data;
		private String error;
		private AjaxFormResponse filtreFormResponse;
		public DatatablesResponse() {
		}
		public int getDraw() {
			return draw;
		}
		public void setDraw(int draw) {
			this.draw = draw;
		}
		public long getRecordsTotal() {
			return recordsTotal;
		}
		public void setRecordsTotal(long recordsTotal) {
			this.recordsTotal = recordsTotal;
		}
		public long getRecordsFiltered() {
			return recordsFiltered;
		}
		public void setRecordsFiltered(long recordsFiltered) {
			this.recordsFiltered = recordsFiltered;
		}
		public List<Map<String, Object>> getData() {
			return data;
		}
		public void setData(List<Map<String, Object>> data) {
			this.data = data;
		}
		public String getError() {
			return error;
		}
		public void setError(String error) {
			this.error = error;
		}
		public AjaxFormResponse getFiltreFormResponse() {
			return filtreFormResponse;
		}
		public void setFiltreFormResponse(AjaxFormResponse filtreFormResponse) {
			this.filtreFormResponse = filtreFormResponse;
		}
		public boolean isFiltreError() {
			return filtreFormResponse != null;
		}
	}

	@Getter @Setter
	public static class DatatablesParams {
		private Integer draw;
		private Integer start;
		private Integer length;
		private String searchValue;
		private Boolean searchRegex;
		private List<Integer> orderColumn = new ArrayList<Integer>();
		private List<String> orderDir = new ArrayList<String>();
		private List<String> columnsData = new ArrayList<String>();
		private List<String> columnsName = new ArrayList<String>();
		private List<Boolean> columnsSearchable = new ArrayList<Boolean>();
		private List<Boolean> columnsOrderable = new ArrayList<Boolean>();
		private List<String> columnsSearchValue = new ArrayList<String>();
		private List<Boolean> columnsSearchRegex = new ArrayList<Boolean>();
		public DatatablesParams(HttpServletRequest request) {
			if (request.getParameter("draw") != null)
				draw = Integer.parseInt(request.getParameter("draw"));
			if (request.getParameter("start") != null)
				start = Integer.parseInt(request.getParameter("start"));
			if (request.getParameter("length") != null)
				length = Integer.parseInt(request.getParameter("length"));
			if (request.getParameter("search[value]") != null)
				searchValue = request.getParameter("search[value]");
			if (request.getParameter("search[regex]") != null)
				searchRegex = Boolean.parseBoolean(request.getParameter("search[regex]"));
			for (int i = 0;; i++) {
				String paramPrefix = "order[" + i + "]";
				if (request.getParameter(paramPrefix + "[column]") != null) {
					orderColumn.add(Integer.parseInt(request.getParameter(paramPrefix + "[column]")));
					orderDir.add(request.getParameter(paramPrefix + "[dir]"));
				} else {
					break;
				}
			}
			for (int i = 0;; i++) {
				String paramPrefix = "columns[" + i + "]";
				if (request.getParameter(paramPrefix + "[data]") != null) {
					String columnData = request.getParameter(paramPrefix + "[data]");
					String columnName = request.getParameter(paramPrefix + "[name]");
					columnsData.add(columnData);
					columnsName.add(columnName);
					columnsSearchable.add(Boolean.parseBoolean(request.getParameter(paramPrefix + "[searchable]")));
					columnsOrderable.add(Boolean.parseBoolean(request.getParameter(paramPrefix + "[orderable]")));
					columnsSearchValue.add(request.getParameter(paramPrefix + "[search][value]"));
					columnsSearchRegex.add(Boolean.parseBoolean(request.getParameter(paramPrefix + "[search][regex]")));
				} else {
					break;
				}
			}
		}
	}



	private static List<PropertyDescriptor> getBeanPropertyDescriptors(
			Object bean) {
		List<PropertyDescriptor> descriptors = new ArrayList<PropertyDescriptor>(
				Arrays.asList(
						PropertyUtils.getPropertyDescriptors(bean)));
		Iterator<PropertyDescriptor> it = descriptors.iterator();
		while (it.hasNext()) {
			PropertyDescriptor pd = it.next();
			if ("class".equals(pd.getName())) {
				it.remove();
				break;
			}
		}
		return descriptors;
	}

	/*private static Map<String, Object> getRowData(
			Object bean,
			String atributId) {
		Map<String, Object> rowData = new HashMap<String, Object>();
		try {
			rowData.put(
					"id",
					PropertyUtils.getProperty(bean, atributId));
		} catch (Exception ex) {
			LOGGER.error(
					"No s'ha pogut llegir la propietat de l'objecte (" +
							"propietatNom=" + atributId + ")",
					ex);
		}
		return rowData;
	}*/

	private static Object getPropietatValor(
			Object registre,
			String propietatNom,
			List<PropertyDescriptor> descriptors) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Object valor = null;
		try {
			int index = Integer.parseInt(propietatNom);
			valor = PropertyUtils.getProperty(registre, descriptors.get(index).getName());
		} catch (NumberFormatException ex) {
			if (propietatNom != null && !propietatNom.isEmpty() && !"<null>".equals(propietatNom)) {
				valor = PropertyUtils.getProperty(registre, propietatNom);
			}
		}
		return valor;
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(DatatablesHelper.class);

}
