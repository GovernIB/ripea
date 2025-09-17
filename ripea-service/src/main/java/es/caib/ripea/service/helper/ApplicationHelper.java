package es.caib.ripea.service.helper;

import es.caib.ripea.persistence.repository.ProcessosInicialsRepository;
import io.micrometer.core.instrument.Measurement;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.distribution.HistogramSnapshot;
import io.micrometer.core.instrument.distribution.ValueAtPercentile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class ApplicationHelper {
	
    @Autowired private ProcessosInicialsRepository processosInicialsRepository;
    @Autowired private MeterRegistry meterRegistry;
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void setProcessAsProcessed(Long id){
		processosInicialsRepository.updateInit(id, false);
	}
	
	public MeterRegistry getMeterRegistry() {
		return meterRegistry;
	}
	
	public void stopTimer(Timer.Sample sample, String metricCode, String... tags) {
        sample.stop(Timer.builder(metricCode)
                .description("Tiempo y resultado")
                .tags(tags)
                .publishPercentiles(0.5, 0.75, 0.95, 0.99)
                .publishPercentileHistogram()
                .register(meterRegistry));
	}
	
    public String getMetriquesJSON() throws Exception {
        List<Map<String, Object>> metricsList = new ArrayList<>();
        for (Meter meter : meterRegistry.getMeters()) {
            Map<String, Object> metricMap = new LinkedHashMap<>();
            metricMap.put("name", meter.getId().getName());

            Map<String, String> tags = new HashMap<>();
            for (Tag tag : meter.getId().getTags()) {
                tags.put(tag.getKey(), tag.getValue());
            }
            metricMap.put("tags", tags);


            // Estructura segons tipus de mètrica
            if (meter instanceof io.micrometer.core.instrument.Timer) {
                io.micrometer.core.instrument.Timer timer = (io.micrometer.core.instrument.Timer) meter;
                HistogramSnapshot snapshot = timer.takeSnapshot();

                // Helpers per percentils
                Map<Double, Double> percentilesMs = new HashMap<>();
                for (ValueAtPercentile v : snapshot.percentileValues()) {
                    percentilesMs.put(v.percentile(), v.value(TimeUnit.MILLISECONDS));
                }
                double p50 = percentilesMs.getOrDefault(0.5, Double.NaN);
                double p75 = percentilesMs.getOrDefault(0.75, Double.NaN);
                double p95 = percentilesMs.getOrDefault(0.95, Double.NaN);
                double p98 = percentilesMs.getOrDefault(0.98, Double.NaN);
                double p99 = percentilesMs.getOrDefault(0.99, Double.NaN);
                double p999 = percentilesMs.getOrDefault(0.999, Double.NaN);

                Map<String, Object> timerData = new LinkedHashMap<>();
                timerData.put("count", snapshot.count());
                timerData.put("duration_units", "milliseconds");
                timerData.put("max", snapshot.max(TimeUnit.MILLISECONDS));
                timerData.put("mean", snapshot.mean(TimeUnit.MILLISECONDS));
                timerData.put("p50", p50);
                timerData.put("p75", p75);
                timerData.put("p95", p95);
                timerData.put("p99", p99);
                timerData.put("rate_units", "calls/second");

                metricMap.put("type", "timer");
                metricMap.put("data", timerData);
            } else if (meter instanceof io.micrometer.core.instrument.Counter) {
                io.micrometer.core.instrument.Counter counter = (io.micrometer.core.instrument.Counter) meter;
                Map<String, Object> counterData = new LinkedHashMap<>();
                counterData.put("count", counter.count());

                metricMap.put("type", "counter");
                metricMap.put("data", counterData);
            } else {
                // Altres tipus de meters: retorn bàsic amb les seves mesures
                List<Map<String, Object>> measurements = new ArrayList<>();
                for (Measurement m : meter.measure()) {
                    measurements.add(Map.of(
                            "statistic", m.getStatistic().name(),
                            "value", m.getValue()
                    ));
                }
                metricMap.put("type", meter.getId().getType().name().toLowerCase());
                metricMap.put("data", Map.of("measurements", measurements));
            }

            metricsList.add(metricMap);
        }

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(metricsList);
    }
}