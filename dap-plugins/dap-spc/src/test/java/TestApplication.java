/*
 * Copyright (c) 2017. For Intelligent Group.
 */

import com.dmsoft.firefly.core.sdkimpl.dai.EnvServiceImpl;
import com.dmsoft.firefly.core.sdkimpl.dai.SourceDataServiceImpl;
import com.dmsoft.firefly.core.sdkimpl.dai.UserPreferenceServiceImpl;
import com.dmsoft.firefly.core.sdkimpl.dataframe.BasicDataFrameFactoryImpl;
import com.dmsoft.firefly.plugin.spc.utils.SpcFxmlAndLanguageUtils;
import com.dmsoft.firefly.sdk.RuntimeContext;
import com.dmsoft.firefly.sdk.dai.dto.TestItemDto;
import com.dmsoft.firefly.sdk.dai.dto.TestItemWithTypeDto;
import com.dmsoft.firefly.sdk.dai.service.EnvService;
import com.dmsoft.firefly.sdk.dai.service.SourceDataService;
import com.dmsoft.firefly.sdk.dai.service.UserPreferenceService;
import com.dmsoft.firefly.sdk.dataframe.DataFrameFactory;
import com.dmsoft.firefly.sdk.utils.enums.LanguageType;
import com.dmsoft.firefly.sdk.utils.enums.TestItemType;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mongodb.MongoClient;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Created by Ethan.Yang on 2018/1/29.
 */
public class TestApplication extends Application {

    static {
        SpcFxmlAndLanguageUtils.isDebug = true;
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
//        List<TestItemWithTypeDto> typeDtoList = Lists.newArrayList();
//        for (int i = 0; i < 20; i++) {
//            TestItemWithTypeDto testItemWithTypeDto = new TestItemWithTypeDto();
//            testItemWithTypeDto.setTestItemName("itemName" + i);
//            typeDtoList.add(testItemWithTypeDto);
//        }
//        envService.setTestItems(typeDtoList);
        DataFrameFactory dataFrameFactory = new BasicDataFrameFactoryImpl();
        MongoClient mongoClient = new MongoClient("localhost");
        MongoTemplate mongoTemplate = new MongoTemplate(mongoClient, "ispc");
        RuntimeContext.registerBean(MongoTemplate.class, mongoTemplate);
        SourceDataServiceImpl sourceDataService = new SourceDataServiceImpl();
        List<String> projectNameList = Lists.newArrayList("Case5.csv");
        Map<String, TestItemDto> testItemDtoMap = sourceDataService.findAllTestItem(projectNameList);
        LinkedHashMap<String, TestItemWithTypeDto> typeDtoList = Maps.newLinkedHashMap();
        for (TestItemDto testItemDto : testItemDtoMap.values()) {
            TestItemWithTypeDto testItemWithTypeDto = new TestItemWithTypeDto();
            testItemWithTypeDto.setTestItemType(TestItemType.VARIABLE);
            testItemWithTypeDto.setTestItemName(testItemDto.getTestItemName());
            testItemWithTypeDto.setUsl(testItemDto.getUsl());
            testItemWithTypeDto.setLsl(testItemDto.getLsl());
            testItemWithTypeDto.setUnit(testItemDto.getUnit());
            typeDtoList.put(testItemDto.getTestItemName(), testItemWithTypeDto);
        }
        envService.setTestItems(typeDtoList);


        RuntimeContext.registerBean(SourceDataService.class, sourceDataService);
        RuntimeContext.registerBean(EnvService.class, envService);
        RuntimeContext.registerBean(UserPreferenceService.class, userPreferenceService);
        RuntimeContext.registerBean(DataFrameFactory.class, dataFrameFactory);
        envService.setActivatedProjectName(projectNameList);
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getClassLoader().getResource("view/spc_setting.fxml"));
        loader.setResources(ResourceBundle.getBundle("i18n.message_en_US_SPC"));
        Parent root = loader.load();


        Scene scene = new Scene(root, 1280, 704);
        scene.getStylesheets().add(getClass().getClassLoader().getResource("css/redfall/main.css").toExternalForm());
        scene.getStylesheets().add(getClass().getClassLoader().getResource("css/spc_app.css").toExternalForm());
        scene.getStylesheets().add(getClass().getClassLoader().getResource("css/charts.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}