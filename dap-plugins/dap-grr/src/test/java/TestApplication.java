/*
 * Copyright (c) 2017. For Intelligent Group.
 */

import com.dmsoft.firefly.core.sdkimpl.dai.EnvServiceImpl;
import com.dmsoft.firefly.core.sdkimpl.dai.UserPreferenceServiceImpl;
import com.dmsoft.firefly.core.sdkimpl.dataframe.BasicDataFrameFactoryImpl;
import com.dmsoft.firefly.plugin.grr.handler.ParamKeys;
import com.dmsoft.firefly.plugin.grr.pipeline.GrrDetailResultJobPipeline;
import com.dmsoft.firefly.plugin.grr.pipeline.GrrSummaryJobPipeline;
import com.dmsoft.firefly.plugin.grr.pipeline.GrrViewDataJobPipeline;
import com.dmsoft.firefly.plugin.grr.utils.GrrFxmlAndLanguageUtils;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.dto.TestItemWithTypeDto;
import com.dmsoft.firefly.sdk.dai.service.EnvService;
import com.dmsoft.firefly.sdk.dai.service.UserPreferenceService;
import com.dmsoft.firefly.sdk.dataframe.DataFrameFactory;
import com.dmsoft.firefly.sdk.job.DefaultJobManager;
import com.dmsoft.firefly.sdk.job.core.JobManager;
import com.dmsoft.firefly.sdk.utils.enums.LanguageType;
import com.google.common.collect.Maps;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.LinkedHashMap;
import java.util.ResourceBundle;

/**
 * Created by Ethan.Yang on 2018/1/29.
 */
public class TestApplication extends Application {
    static {
        GrrFxmlAndLanguageUtils.isDebug = true;
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        UserPreferenceService userPreferenceService = new UserPreferenceServiceImpl();
        EnvService envService = new EnvServiceImpl() {
            @Override
            public LanguageType getLanguageType() {
                return LanguageType.EN;
            }
        };
        DefaultJobManager jobManager = new DefaultJobManager();
        LinkedHashMap<String, TestItemWithTypeDto> typeDtoList = Maps.newLinkedHashMap();
        for (int i = 0; i < 20; i++) {
            TestItemWithTypeDto testItemWithTypeDto = new TestItemWithTypeDto();
            testItemWithTypeDto.setTestItemName("itemName" + i);
            typeDtoList.put("itemName" + i, testItemWithTypeDto);
        }
        envService.setTestItems(typeDtoList);
        DataFrameFactory dataFrameFactory = new BasicDataFrameFactoryImpl();
        RuntimeContext.registerBean(EnvService.class, envService);
        RuntimeContext.registerBean(UserPreferenceService.class, userPreferenceService);
        RuntimeContext.registerBean(DataFrameFactory.class, dataFrameFactory);
        RuntimeContext.registerBean(DefaultJobManager.class, jobManager);

        FXMLLoader fxmlLoader = GrrFxmlAndLanguageUtils.getLoaderFXML("view/grr.fxml");
        Parent root = fxmlLoader.load();


        Scene scene = new Scene(root, 1280, 704);
        scene.getStylesheets().add(getClass().getClassLoader().getResource("css/redfall/main.css").toExternalForm());
        scene.getStylesheets().add(getClass().getClassLoader().getResource("css/grr_app.css").toExternalForm());
        scene.getStylesheets().add(getClass().getClassLoader().getResource("css/grr_chart.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
        initJob();
    }

    private void initJob() {
        JobManager manager = RuntimeContext.getBean(JobManager.class);
        manager.initializeJob(ParamKeys.GRR_ANALYSIS_JOB_PIPELINE, new GrrSummaryJobPipeline());
        manager.initializeJob(ParamKeys.GRR_DETAIL_ANALYSIS_JOB_PIPELINE, new GrrDetailResultJobPipeline());
        manager.initializeJob(ParamKeys.GRR_VIEW_DATA_JOB_PIPELINE, new GrrViewDataJobPipeline());
    }
}