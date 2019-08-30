package com.ifc.populationorderdeterminant.service;

import com.ifc.populationorderdeterminant.app.PropertiesProvider;
import com.ifc.populationorderdeterminant.entity.Function;
import com.ifc.populationorderdeterminant.repository.FunctionDAO;
import com.ifc.populationorderdeterminant.utils.RegexUtil;
import com.ifc.populationorderdeterminant.utils.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class FunctionService {

    private List<String> excludedFunctions;
    private static final String HINT = " Add the function to a list of excluded functions in the configuration file.";
    private static final String FIND_VIEW_NAME_PATTERN = "(?i)from %s.(.+?)\\b";

    public FunctionService() {
        excludedFunctions = PropertiesProvider.getExcludedFunctions();
    }

    public List<Function> getAllPopulateFunctionsInSchema(String schema) {
        List<Function> functions = FunctionDAO.getAllPopulateFunctionsInSchema(schema);

        if (CollectionUtils.isEmpty(functions)) {
            throw new RuntimeException("Unable to load functions");
        }

        return functions.stream()
                .filter(function -> !isFunctionExcluded(function.getName()))
                .peek(this::validateFunctionDefinition)
                .collect(Collectors.toList());
    }

    public String getViewNameByFunction(Function function) {
        final String pattern = String.format(FIND_VIEW_NAME_PATTERN, function.getSchema());

        Optional<String> viewNameOptional = RegexUtil.substring(function.getDefinition(), pattern);

        if (!viewNameOptional.isPresent() || StringUtils.isEmpty(viewNameOptional.get())) {
            throw new RuntimeException("Function " + function + " doesn't match any views." + HINT);
        }
        return StringUtil.validateString(viewNameOptional.get());
    }

    public String getTableNameByFunction(Function function) {
        String tableName = StringUtils.substringBetween(
                function.getDefinition(),
                "insert into " + function.getSchema() + ".",
                " (");

        if (StringUtils.isEmpty(tableName)) {
            throw new RuntimeException("Function " + function + " doesn't match any tables." + HINT);
        }
        return StringUtil.validateString(tableName);
    }

    private boolean isFunctionExcluded(String functionName) {
        return excludedFunctions.stream()
                .anyMatch(function -> Objects.equals(function, functionName));
    }

    private void validateFunctionDefinition(Function function) {
        if (StringUtils.isEmpty(function.getDefinition())) {
            throw new RuntimeException("Unable to get the definition of the function " + function + "." + HINT);
        }
    }

}