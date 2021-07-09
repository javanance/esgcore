package com.gof.model.hw;


import static java.util.stream.Collectors.toList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.gof.entity.EsgParamBiz;
import com.gof.entity.EsgMst;
import com.gof.entity.EsgParamHis;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Hw1fCalibParas implements Serializable {
	
	private static final long serialVersionUID = -3028479667862393941L;

	//@Id
	private String baseDate;
	
	private Integer monthSeq;
	
	private String matCd;
	
	private Double alpha;
	
	private Double sigma;
	
	
	public Hw1fCalibParas() {}
	
	public List<EsgParamHis> convert(String irModelType, String paramCalcCd) {
		List<EsgParamHis> rstList = new ArrayList<EsgParamHis>();
		
		EsgParamHis alphaRst  = new EsgParamHis();
		if(matCd.equals("M0120") || matCd.equals("M0240")) {
			alphaRst.setBaseYymm(baseDate.substring(0,6));
			alphaRst.setIrModelTyp(irModelType);
			alphaRst.setCalibModel(paramCalcCd);
			alphaRst.setParamTypCd("ALPHA");
			alphaRst.setMatCd(matCd);
			alphaRst.setParamVal(alpha);
			rstList.add(alphaRst);
		}
		
		EsgParamHis sigamRst  = new EsgParamHis();
		
		sigamRst.setBaseYymm(baseDate.substring(0, 6));
		sigamRst.setIrModelTyp(irModelType);
		sigamRst.setCalibModel(paramCalcCd);
		sigamRst.setParamTypCd("SIGMA");
		sigamRst.setMatCd(matCd);
		sigamRst.setParamVal(sigma);
		
		rstList.add(sigamRst);
		
		return rstList;
	}
	
	public List<EsgParamHis> convert(EsgMst mst) {
		List<EsgParamHis> rstList = new ArrayList<EsgParamHis>();
		
		EsgParamHis alphaRst  = new EsgParamHis();
		
		if(matCd.equals("M0120") || matCd.equals("M0240")) {
			alphaRst.setBaseYymm(baseDate.substring(0,6));
			
			alphaRst.setIrModelId(mst.getIrModelId());
			alphaRst.setIrModelTyp(mst.getIrModelTyp().name());
			alphaRst.setCalibModel(mst.getCalibModel().name());
			alphaRst.setParamTypCd("ALPHA");
			alphaRst.setMatCd(matCd);
			alphaRst.setParamVal(alpha);
			rstList.add(alphaRst);
		}
		
		EsgParamHis sigamRst  = new EsgParamHis();
		
		sigamRst.setBaseYymm(baseDate.substring(0, 6));
		sigamRst.setIrModelId(mst.getIrModelId());
		sigamRst.setIrModelTyp(mst.getIrModelTyp().name());
		sigamRst.setCalibModel(mst.getCalibModel().name());
		sigamRst.setParamTypCd("SIGMA");
		sigamRst.setMatCd(matCd);
		sigamRst.setParamVal(sigma);
		
		rstList.add(sigamRst);
		
		return rstList;
	}

	
	public static List<Hw1fCalibParas> convertFrom(List<EsgParamBiz> bizParam) {
		
		List<Hw1fCalibParas> rstList = new ArrayList<Hw1fCalibParas>();
		
		double alpha1 = bizParam.stream().filter(s->s.getMatCd().equals("M0240")).mapToDouble(EsgParamBiz::getApplParamVal).sum();
		double alpha2 = bizParam.stream().filter(s->s.getMatCd().equals("M1200")).mapToDouble(EsgParamBiz::getApplParamVal).sum();
		
		
		List<EsgParamBiz> sigmaList = bizParam.stream().filter(s-> s.getParamTypCd().equals("SIGMA")).collect(toList());
		
		for(EsgParamBiz aa : sigmaList) {
			Hw1fCalibParas temp = new Hw1fCalibParas();
			temp.baseDate= aa.getBaseYymm();
			temp.matCd = aa.getMatCd();
			temp.monthSeq = Integer.valueOf(aa.getMatCd().split("M")[1]);
			temp.sigma = aa.getApplParamVal();

			temp.alpha = temp.monthSeq < 240? alpha1: alpha2;
			
			rstList.add(temp);
		}
		
		return rstList;
	}
}
