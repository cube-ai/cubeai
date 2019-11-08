package com.wyy.service.docker;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.io.*;
import java.util.*;
import java.util.regex.Pattern;


public final class PythonUtil {

	public static Boolean generateRequirementTxt(File outputFolder, JSONArray requirementsJSONArray) {

        File outRequirementFile = new File(outputFolder, "requirements.txt");
        ArrayList<Requirement> requirements = new ArrayList<>();
        for (Object requirementNode : requirementsJSONArray) {
            Requirement requirement = new Requirement();
            requirement.name = ((JSONObject)requirementNode).getString("name");
            requirement.version = ((JSONObject)requirementNode).getString("version");
//            if (requirement.name.equals("tensorflow")) {
//                requirement.version = "1.13.1";
//            }
            if (requirement.version != null) {
                requirement.operator = "==";
            }
            requirements.add(requirement);
        }

		try {
			StringBuilder reqAsString = new StringBuilder();
			for (Requirement pipRequirement : requirements) {
				if (pipRequirement.version != null) {
					reqAsString.append(pipRequirement.name + pipRequirement.operator + pipRequirement.version + "\n");
				} else
					reqAsString.append(pipRequirement.name + "\n");
			}
			FileWriter writer = new FileWriter(outRequirementFile);
			writer.write(reqAsString.toString().trim());
			writer.close();
			return true;
		} catch (IOException e) {
            return false;
		}
	}

    public static void generateDockerfile(File outputFolder, JSONArray requirementsJSONArray, String pythonVersion) {
	    boolean sklearnFlag = false;
	    boolean tfFlag = false;
        boolean pyVer3_5Flag = false;
        boolean pyVer3_6Flag = false;
        boolean pyVer3_7Flag = false;

        for (Object requirementNode : requirementsJSONArray) {
            if( ((JSONObject)requirementNode).getString("name").equals("scikit-learn")){
                sklearnFlag = true;
            } else if(((JSONObject)requirementNode).getString("name").equals("tensorflow")){
                tfFlag = true;
            }
        }
        pyVer3_5Flag = Pattern.matches("3.5.*", pythonVersion);
        pyVer3_6Flag = Pattern.matches("3.6.*", pythonVersion);
        pyVer3_7Flag = Pattern.matches("3.7.*", pythonVersion);

        File outDockerfile = new File(outputFolder, "Dockerfile");
        if( pyVer3_6Flag){
            new File(outputFolder, "Dockerfile.python3.6").renameTo(outDockerfile);
            new File(outputFolder, "Dockerfile.sk-tf").delete();
            new File(outputFolder, "Dockerfile.base").delete();
            new File(outputFolder, "Dockerfile.sklearn").delete();
            new File(outputFolder, "Dockerfile.tensorflow").delete();
            new File(outputFolder, "Dockerfile.python3.7").delete();
        } else if( pyVer3_7Flag){
            new File(outputFolder, "Dockerfile.python3.7").renameTo(outDockerfile);
            new File(outputFolder, "Dockerfile.sk-tf").delete();
            new File(outputFolder, "Dockerfile.base").delete();
            new File(outputFolder, "Dockerfile.sklearn").delete();
            new File(outputFolder, "Dockerfile.tensorflow").delete();
            new File(outputFolder, "Dockerfile.python3.6").delete();
        } else{
            if( sklearnFlag && tfFlag){
                new File(outputFolder, "Dockerfile.sk-tf").renameTo(outDockerfile);
                new File(outputFolder, "Dockerfile.base").delete();
                new File(outputFolder, "Dockerfile.sklearn").delete();
                new File(outputFolder, "Dockerfile.tensorflow").delete();
                new File(outputFolder, "Dockerfile.python3.6").delete();
                new File(outputFolder, "Dockerfile.python3.7").delete();
            } else if(sklearnFlag){
                new File(outputFolder, "Dockerfile.sklearn").renameTo(outDockerfile);
                new File(outputFolder, "Dockerfile.base").delete();
                new File(outputFolder, "Dockerfile.tensorflow").delete();
                new File(outputFolder, "Dockerfile.sk-tf").delete();
                new File(outputFolder, "Dockerfile.python3.6").delete();
                new File(outputFolder, "Dockerfile.python3.7").delete();
            } else if(tfFlag){
                new File(outputFolder, "Dockerfile.tensorflow").renameTo(outDockerfile);
                new File(outputFolder, "Dockerfile.base").delete();
                new File(outputFolder, "Dockerfile.sklearn").delete();
                new File(outputFolder, "Dockerfile.sk-tf").delete();
                new File(outputFolder, "Dockerfile.python3.6").delete();
                new File(outputFolder, "Dockerfile.python3.7").delete();
            } else {
                new File(outputFolder, "Dockerfile.base").renameTo(outDockerfile);
                new File(outputFolder, "Dockerfile.sklearn").delete();
                new File(outputFolder, "Dockerfile.tensorflow").delete();
                new File(outputFolder, "Dockerfile.sk-tf").delete();
                new File(outputFolder, "Dockerfile.python3.6").delete();
                new File(outputFolder, "Dockerfile.python3.7").delete();
            }
        }
    }

}
