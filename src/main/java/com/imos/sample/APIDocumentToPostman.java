/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.imos.sample;

import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author alok
 */
public class APIDocumentToPostman {

    private final Map<String, BiConsumer<String, APIData>> API_MAP = new HashMap<>();

    private final Map<String, Map<String, String>> DEFINE_MAP = new HashMap<>();

    private APIData apiData;

    private Pattern pa = Pattern.compile("((/\\w+)|(/\\{\\w+\\}))+");

    private Matcher ma;

    {
        API_MAP.put("@api", (d, ins) -> {
            ins.setApiMethod(d.substring(d.indexOf("{") + 1, d.indexOf("}")).toUpperCase());
            ma = pa.matcher(d.substring(d.indexOf("}") + 1).trim());
            ins.setApiUrl(ma.find() ? ma.group() : "");
            ins.setApi(d);
        });
        API_MAP.put("@apiVersion", (d, ins) -> {
            ins.setApiVersion(d);
        });
        API_MAP.put("@apiGroup", (d, ins) -> {
            ins.setApiGroup(d);
        });
        API_MAP.put("@apiName", (d, ins) -> {
            ins.setApiName(d);
        });
        API_MAP.put("@apiDescription", (d, ins) -> {
            ins.setApiDescription(d);
        });
        API_MAP.put("@apiPermission", (d, ins) -> {
            ins.setApiPermission(d);
        });
        API_MAP.put("@apiuse", (d, ins) -> {
            ins.getApiUses().add(d);
        });
        API_MAP.put("@apiUse", (d, ins) -> {
            ins.getApiUses().add(d);
        });
        API_MAP.put("@apiParam", (d, ins) -> {
            ins.setApiParam(d);
        });
        API_MAP.put("@apiSuccess", (d, ins) -> {
            ins.setApiSuccess(d);
        });
//        API_MAP.put("@apiDefine", (d, ins) -> {
//            ins.setApiDefine(d);
//        });
//        API_MAP.put("@apiHeader", (d, ins) -> {
//            ins.setApiHeader(d);
//        });
//        API_MAP.put("@apiParamExample", (d, ins) -> {
//            ins.setApiParamExample(d);
//        });
//        API_MAP.put("@apiError", (d, ins) -> {
//            ins.setApiError(d);
//        });
//        API_MAP.put("@apiErrorExample", (d, ins) -> {
//            ins.setApiErrorExample(d);
//        });
//        API_MAP.put("@apiSuccessExample", (d, ins) -> {
//            ins.setApiSuccessExample(d);
//        });
    }

    public static void main(String[] args) {
        new APIDocumentToPostman().extract();
    }

    private void extract() {
        int startIndex, endIndex;
        String startTag = "/**", endTag = "*/", fileType = ".java";
        List<APIData> apiDataList = new ArrayList<>();
        List<String> data = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get("/home/alok/Tools/InviRepo/AdminServices/AdminConsole/src/main/java/com/invicara/admin/console/rest"))) {
            for (Path entry : stream) {
                if (entry.toFile().getAbsolutePath().endsWith(fileType)) {
                    String builder = new String(Files.readAllBytes(entry), StandardCharsets.UTF_8);
                    startIndex = endIndex = 0;
                    while (builder.indexOf(startTag, startIndex) > 0) {
                        startIndex = builder.indexOf(startTag, startIndex);
                        endIndex = builder.indexOf(endTag, endIndex) + 2;
                        String str = builder.substring(startIndex, endIndex);
                        data.add(str);
                        startIndex = endIndex;
                    }
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(APIDocumentToPostman.class.getName()).log(Level.SEVERE, null, ex);
        }
        Iterator<String> itr = data.iterator();
        String annotation = "@api";
        String separator = "\r\n";
        while (itr.hasNext()) {
            String[] lines = itr.next().split(separator);
            boolean annoContains = false;

            for (String line : lines) {
                if (line.contains(annotation)) {
                    annoContains = true;
                }
            }
            if (!annoContains) {
                itr.remove();
            }
        }
        String lineBreak = "\n";
        String pattern = "@\\w+\\s";
        String definePattern = "@apiDefine";
        Pattern p = Pattern.compile(pattern);
        Pattern dp = Pattern.compile(definePattern);
        data.stream().forEach(d -> {
            Matcher dm = dp.matcher(d);
            if (dm.find()) {
//                System.out.println(d);
                String str = d;
                str = str.substring(str.indexOf(definePattern) + definePattern.length());
                String key = str.substring(0, str.indexOf(lineBreak)).trim();
//                System.out.println(key);
                str = str.substring(str.indexOf(lineBreak) + 1).trim();
                Matcher m = p.matcher(str);
                String anno;
                int removeAnnoLength;
                if (m.find()) {
                    anno = m.group();
                    anno = anno.trim();
                    removeAnnoLength = anno.length();
                    str = str.substring(str.indexOf(anno) + removeAnnoLength);
//                    str = str.substring(str.indexOf(lineBreak) + 1);
                    str = str.replaceAll("\\*/", "");
                    str = str.replaceAll("\\s*\\*\\s*", "");
                    str = str.replaceAll("\n", "");
                    str = str.replaceAll("\r", "");

                    Map<String, String> map = DEFINE_MAP.get(key);
                    if (map == null) {
                        map = new HashMap<>();
                    }
                    map.put(anno, str.trim());
                    DEFINE_MAP.put(key, map);
                } else {
                    System.out.println("#" + str);
                }
            } else {
                String[] lines = d.split(lineBreak);
                String removeAnno;
                int removeAnnoLength;
                apiData = new APIData();
                for (String line : lines) {
                    if (line.contains(annotation)) {
                        Matcher m = p.matcher(line);
                        if (m.find()) {
                            removeAnno = m.group();
                            removeAnnoLength = removeAnno.length();
                            line = line.substring(line.indexOf(removeAnno) + removeAnnoLength);
                            API_MAP.get(removeAnno.trim()).accept(line, apiData);
                        } else {
                            System.out.println("#" + line);
                        }
                    }
                }
//                System.out.println(apiData);
                apiDataList.add(apiData);
            }

        });
        System.out.println(data.size());
        Map<String, List<APIData>> group = apiDataList.stream().collect(Collectors.groupingBy(d -> {
            return d.getApiGroup().toUpperCase();
        }));
        System.out.println(group.size());
        System.out.println(group);
        System.out.println(DEFINE_MAP.size());
        System.out.println(DEFINE_MAP);

        group.entrySet().forEach(o -> {
            System.out.println(o.getKey());
            o.getValue().forEach(d -> {
                System.out.println(d);
                d.getApiUses().forEach(u -> {
                    System.out.println(DEFINE_MAP.get(u));
                });
                System.out.println("");
            });
            System.out.println("");
        });

        JSONObject postman = new JSONObject();
        postman.put("variables", new JSONArray());
        JSONObject info = new JSONObject();
        postman.put("info", info);
        JSONArray items = new JSONArray();

//        JSONObject item = new JSONObject();
//        item.put("name", "");
//        JSONObject request = new JSONObject();
//        request.put("url", "");
//        request.put("method", "");
//        JSONArray headers = new JSONArray();
//        JSONObject header = new JSONObject();
//        header.put("key", "");
//        header.put("value", "");
//        header.put("description", "");
//        headers.put(header);
//        request.put("header", headers);`` ````
//        JSONObject body = new JSONObject();
//        body.put("mode", "");
//        body.put("raw", "");
//        request.put("body", body);
//        item.put("request", request);
//        JSONArray responses = new JSONArray();
//        JSONObject response = new JSONObject();
//        responses.put(response);
//        item.put("response", responses);
//        items.put(item);
//        postman.put("item", items);
        //System.out.println(postman);
        group.entrySet().forEach(o -> {
            System.out.println(o.getKey());
            o.getValue().forEach(d -> {
                JSONObject item = new JSONObject();
                item.put("name", d.getApiName());
                JSONObject request = new JSONObject();
                request.put("url", d.getApiUrl());
                request.put("method", d.getApiMethod());

                JSONArray headers = new JSONArray();
                JSONObject body = new JSONObject();
                d.getApiUses().stream().forEach(s -> {
                    if (DEFINE_MAP.get(s).keySet().contains("@apiHeader")) {
                        String values[] = DEFINE_MAP.get(s).get("@apiHeader").split(" ");
                        JSONObject header = new JSONObject();
                        header.put("key", values[1]);
                        header.put("value", values[2]);
                        header.put("description", "");
                        headers.put(header);
                    } else if (DEFINE_MAP.get(s).keySet().contains("@apiParamExample")) {
                        String dat = DEFINE_MAP.get(s).get("@apiParamExample");
                        System.out.println(dat);
                        try {
                            dat = dat.substring(dat.indexOf("{"));
                            dat = dat.replaceAll("Long", "0");
                            dat = dat.replaceAll("String", "\"\"");
                            dat = dat.replaceAll("Integer", "0");
                            dat = dat.replaceAll("Boolean", "true");
                            System.out.println(dat);
                            if (d.getApiMethod().equalsIgnoreCase("POST")
                                || d.getApiMethod().equalsIgnoreCase("PUT")) {
                                body.put("mode", "raw");
                                body.put("raw", dat);
                            }
                        } catch (Exception e) {
                            System.out.println(dat);
                        }

                    }
                });
                request.put("header", headers);

                if (d.getApiMethod().equalsIgnoreCase("GET")) {
                    body.put("mode", "formdata");
                    JSONArray formdata = new JSONArray();
                    JSONObject fd = new JSONObject();
                    fd.put("key", "file");
                    fd.put("type", "file");
                    fd.put("enabled", true);
                    fd.put("value", "file");
                    formdata.put(fd);
                    body.put("formdata", formdata);
                } else if (d.getApiMethod().equalsIgnoreCase("DELETE")) {
                    body.put("mode", "raw");
                    body.put("raw", "");
                }

                request.put("body", body);
                request.put("description", d.getApiDescription());
                item.put("request", request);
                JSONArray responses = new JSONArray();
                JSONObject response = new JSONObject();
//                responses.put(response);
                item.put("response", responses);
                System.out.println(d);
                System.out.println(item.toString());
                items.put(item);
                d.getApiUses().forEach(u -> {
                    System.out.println(DEFINE_MAP.get(u));
                });
                System.out.println("");
            });

        });
        System.out.println("");
        System.out.println(items.toString());
    }
}
