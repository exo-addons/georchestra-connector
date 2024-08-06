package org.exoplatform.addons.georchestra.job;

import org.exoplatform.addons.georchestra.services.GeorchestraService;
import org.exoplatform.addons.georchestra.storage.model.GeorchestraQueue;
import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.ArrayList;
import java.util.List;

@DisallowConcurrentExecution
public class GeorchestraQueueJob implements Job {
  private static final Log LOG = ExoLogger.getLogger(GeorchestraQueueJob.class);
  private GeorchestraService georchestraService;

  @Override
  public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    georchestraService = CommonsUtils.getService(GeorchestraService.class);
    LOG.debug("Start treating Georchestra queue");
    List<GeorchestraQueue> items;
    int offset = 0;
    int limit=10;
    List<GeorchestraQueue> itemsToRemove= new ArrayList<>();
    do {
      items = georchestraService.findGeorchestraQueue(offset,limit);
      items.forEach(georchestraQueue -> {
        if (georchestraService.treatQueueItem(georchestraQueue)) {
          itemsToRemove.add(georchestraQueue);
        }
      });
      offset+=limit;
    } while (items.size() == limit);

    georchestraService.deleteAllGeorchestraQueue(itemsToRemove);
    LOG.debug("End treating Georchestra queue");


  }
}
