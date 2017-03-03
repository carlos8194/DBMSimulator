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
 * This class is in charge of generating the simulation reports that contain both the global statistics and an individual
 * page that details an individual iteration statistic.
 * It implements one method which builds the index and displays the global statistics and another method which generates
 * all the individual iteration's report.
 *
 */
public class SimulationReports {

    /**
     * Class constructor.
     */
    public SimulationReports(){}

    /**
     * This is the main method called to generate the statistics reports, it uses the apache velocity template engine
     * and calls to different methods to create the index and the individual reports.
     * It creates and  stores the statistics in the /src/statistics/ folder.
     * It obtains the templates from the /src/resources folder.
     * @param globalStatistics the object containing the simulation's statistics.
     * @throws Exception if there is any problem creating the folder or reports.
     */
    public void generateReports(SimulatorStatistics globalStatistics) throws Exception {
        VelocityEngine velocityEngine = new VelocityEngine();
        velocityEngine.init();
        File statisticsFolder = new File("./src/statistics/");
        if(statisticsFolder.exists()) {
            FileUtils.cleanDirectory(statisticsFolder);
        }
        else {
            statisticsFolder.mkdir();
        }
        generateIndex(globalStatistics, velocityEngine);
        File iterations = new File("./src/statistics/iterations");
        iterations.mkdir();
        generateIndividualReports(globalStatistics, velocityEngine);


    }

    /**
     * This is the method which creates the index page. The index displays the simulation global statistics and contains
     * a link to each iteration individual report.
     * @param globalStatistics the object containing the simulation's statistics.
     * @param velocityEngine the template engine used to create the index html page
     * @throws IOException if there is any problem saving the file or retrieving the template from resources.
     */
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

    /**
     * This method creates each individual iteration statistics report. The individual report displays the global stadistics
     * as well as the specific iteration report of performance measures.
     * It obtains a list with each simulation iteration run from the globalStatistics received as a parameter.
     * @param globalStatistics the object containing the simulation's statistics.
     * @param velocityEngine the template engine used to create the reports html pages.
     * @throws IOException if there is any problem saving the file or retrieving the template from resources.
     */
    private void generateIndividualReports(SimulatorStatistics globalStatistics, VelocityEngine velocityEngine) throws IOException {
        List<SimulatorStatistics> statisticsList = globalStatistics.getStatisticsList();
        Template iterationTemplate = velocityEngine.getTemplate( "./src/main/resources/iteration_report_template.vm" );
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
            iterationTemplate.merge( context, writer );
            String result = writer.toString();
            String path = "./src/statistics/iterations/iteration_"+(i+1)+".html";
            File file = new File(path);
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(result.getBytes());
            outputStream.close();

        }

    }

}
