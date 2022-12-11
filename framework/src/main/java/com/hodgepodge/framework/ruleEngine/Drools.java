package main.java.com.hodgepodge.framework.ruleEngine;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.IOUtils;
import org.drools.compiler.commons.jci.problems.CompilationProblem;
import org.drools.compiler.compiler.DescrBuildError;
import org.drools.compiler.compiler.FunctionError;
import org.drools.compiler.lang.descr.RuleDescr;
import org.drools.core.io.impl.ReaderResource;
import org.drools.core.util.DroolsStreamUtils;
import org.kie.api.command.Command;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.ExecutionResults;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;
import org.kie.internal.builder.KnowledgeBuilderError;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.command.CommandFactory;
import org.kie.internal.runtime.StatelessKnowledgeSession;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.jar.JarInputStream;

@Slf4j
public class Drools {

    private static KnowledgeBase kBase = KnowledgeBaseFactory.newKnowledgeBase();

    public void build() throws Exception {
        KnowledgeBuilder kbuilder = createBuilderOnly();

        String generatedSource = "";

        kbuilder.add(new ReaderResource(new StringReader(generatedSource)), ResourceType.DRL);

        byte[] bytes = buildByteInfo(kbuilder);

        List<Map<String, Object>> errorInfo = buildErrorInfo(kbuilder);
        if (!CollectionUtils.isEmpty(errorInfo)) {
            log.error("Package build failed with msg:\n{}", errorInfo);
        }
    }

    private KnowledgeBuilder createBuilderOnly() throws IOException {
        List<JarInputStream> jars = new ArrayList<JarInputStream>();
        //添加依赖jar包
        jars.add(new JarInputStream(new ByteArrayInputStream("".getBytes(StandardCharsets.UTF_8))));

        Properties props = new Properties();
        props.setProperty("drools.dialect.java.compiler", "JANINO");

        KnowledgeBuilderConfiguration conf = KnowledgeBuilderFactory
                .newKnowledgeBuilderConfiguration(null, this.getClass().getClassLoader());
        return KnowledgeBuilderFactory.newKnowledgeBuilder(conf);
    }

    public byte[] buildByteInfo(KnowledgeBuilder kBuilder) throws Exception {
        byte[] bytes = null;

        //转换为二进制流
        Map<String, byte[]> builtPkgsMap = new HashMap<String, byte[]>();
        builtPkgsMap.put("single", DroolsStreamUtils.streamOut(kBuilder.getKnowledgePackages()));

        ByteArrayOutputStream bos = null;
        ObjectOutputStream oos = null;
        try {
            bos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(bos);
            oos.writeObject(builtPkgsMap);
            bytes = bos.toByteArray();
        } catch (Exception e) {

        } finally {
            IOUtils.closeQuietly(bos);
            IOUtils.closeQuietly(oos);
        }

        return bytes;
    }

    public List<Map<String, Object>> buildErrorInfo(KnowledgeBuilder builder) {
        List<Map<String, Object>> result = Lists.newArrayListWithCapacity(50);
        boolean hasErrors = builder.hasErrors();
        if (hasErrors) {
            log.info("Package validated failed with msg:\n{}", builder.getErrors());
            for (KnowledgeBuilderError error : builder.getErrors()) {
                if (error instanceof FunctionError) {
                    FunctionError fe = (FunctionError) error;
                    if (fe.getObject() instanceof CompilationProblem[]) {
                        CompilationProblem[] cp = (CompilationProblem[]) fe.getObject();
                        for (int i = 0; i < cp.length; i++) {
                            Map<String, Object> map = Maps.newHashMap();
                            map.put("line", fe.getLines()[i] + 1);
                            map.put("message", cp[i].getMessage());
                            map.put("serverity", error.getSeverity());
                            map.put("name", fe.getFunctionDescr().getClassName());
                            result.add(map);
                        }
                    }
                } else if (error instanceof DescrBuildError) {
                    DescrBuildError dbe = (DescrBuildError) error;
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("line", dbe.getLine());
                    if (dbe.getParentDescr() instanceof RuleDescr) {
                        map.put("name", ((RuleDescr) dbe.getParentDescr()).getNamespace() + " " + ((RuleDescr) dbe.getParentDescr()).getName());
                    }
                    map.put("message", dbe.getMessage());
                    map.put("serverity", dbe.getSeverity());
                    result.add(map);
                } else {
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("serverity", error.getSeverity());
                    map.put("message", error.getMessage());
                    result.add(map);
                }
            }
        }

        return result;
    }

    public void execute() {
        List<Command> commands = Lists.newArrayList();
        commands.add(CommandFactory.newQuery("risk", "list all risks from working memory"));
        StatelessKnowledgeSession session = kBase.newStatelessKnowledgeSession();
        ExecutionResults execute = session.execute(CommandFactory.newBatchExecution(commands));
        execute.getValue("");
    }
}
