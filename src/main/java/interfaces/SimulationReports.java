package interfaces;
import java.io.*;
import java.util.List;

import dbms.SimulatorStatistics;
import org.apache.commons.io.FileUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.Template;
import org.apache.velocity.app.VelocityEngine;

import query.QueryType;


/**
 * Created by Rodrigo on 2/7/2017.
 */
public class SimulationReports {

    public SimulationReports(){}

    public void generateReports(SimulatorStatistics statistics) throws Exception {
        VelocityEngine velocityEngine = new VelocityEngine();
        velocityEngine.init();
        FileUtils.cleanDirectory(new File("./src/statistics/"));
        generateIndex(statistics, velocityEngine);
        File iterations = new File("./src/statistics/iterations");
        iterations.mkdir();
        generateIndividualReports(statistics, velocityEngine);


    }

    private void generateIndex(SimulatorStatistics globalStatistics, VelocityEngine velocityEngine) throws IOException {
        Template indexTemplate = velocityEngine.getTemplate( "./src/main/resources/index_template.vm" );
        VelocityContext context = new VelocityContext();
        context.put("statistics", globalStatistics);
        context.put("DDL", QueryType.DDL);
        context.put("UPDATE", QueryType.UPDATE);
        context.put("JOIN", QueryType.JOIN);
        context.put("SELECT",QueryType.SELECT);
        StringWriter writer = new StringWriter();
        indexTemplate.merge( context, writer );
        String result = writer.toString();
        FileOutputStream outputStream = new FileOutputStream(new File("./src/statistics/index.html"));
        outputStream.write(result.getBytes());
        outputStream.close();
    }

    private void generateIndividualReports(SimulatorStatistics globalStatistics, VelocityEngine velocityEngine) throws IOException {
        List<SimulatorStatistics> statisticsList = globalStatistics.getStatisticsList();
        Template indexTemplate = velocityEngine.getTemplate( "./src/main/resources/iteration_report_template.vm" );
        for (int i = 0; i < statisticsList.size() ; i++) {
            SimulatorStatistics statistics = statisticsList.get(i);
            VelocityContext context = new VelocityContext();
            context.put("statistics", statistics);
            context.put("iteration", i+1);
            context.put("DDL", QueryType.DDL);
            context.put("UPDATE", QueryType.UPDATE);
            context.put("JOIN", QueryType.JOIN);
            context.put("SELECT",QueryType.SELECT);
            StringWriter writer = new StringWriter();
            indexTemplate.merge( context, writer );
            String result = writer.toString();
            String path = "./src/statistics/iterations/iteration_"+(i+1)+".html";
            File file = new File(path);
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(result.getBytes());
            outputStream.close();

        }

    }

}
