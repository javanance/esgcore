package com.gof.model.hw;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.math3.random.GaussianRandomGenerator;
import org.apache.commons.math3.random.MersenneTwister;

import com.gof.entity.IrCurveHis;
import com.gof.util.DateUtil;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@Slf4j
public class Hw1fSimulationKics extends IrmodelHw {
	
	protected int[]         monthSeq;
	protected double[]      timeFactor;
	protected String[]      matCd;
	protected double[]      spotContBase;
	protected double[]      spotDiscBase;
	protected double[]      fwdContBase;
	protected double[]      fwdDiscBase;	
	protected double[]      dcntFactorBase;
	
	protected int           scenNum;
	protected double        dt;	
	
	protected double[]      alpha;
	protected double[]      sigma;
	protected double[]      theta;
	protected int[]         alphaPiece;
	protected int[]         sigmaPiece;	
	protected String        mode;
	protected boolean       priceAdj;

	protected double[][]    randNum;
	protected double[][]    sRateScen;
	protected double[][]    spotContScen;
	protected double[][]    spotDiscScen;
	protected double[][]    fwdContScen;
	protected double[][]    fwdDiscScen;	
	protected double[][]    dcntFactorScen;
	
	protected double[]      sRateMean;
	protected double[]      spotContMean;
	protected double[]      spotDiscMean;	
	protected double[]      dcntFactorMean;
	protected double[]      fwdContMean;
	protected double[]      fwdDiscMean;	
	
	protected double        ltfr;	
	protected int           ltfrT;
	protected int           prjYear;
	protected int           prjMonth;	
	protected int           prjInterval;	
	protected double        lastLiquidPoint;
	
	protected int           duraMonth;
	protected double[][]    bondYieldCont;	
	protected double[][]    bondYieldDisc;
	protected double[]      bondYieldContMean;
	protected double[]      bondYieldDiscMean;	
	
	protected List<SmithWilsonRslt> swRsltList = new ArrayList<SmithWilsonRslt>();
	protected TreeMap<String, Double> alphaMap   = new TreeMap<String, Double>();
	protected TreeMap<String, Double> sigmaMap   = new TreeMap<String, Double>();
	

	public Hw1fSimulationKics(LocalDate baseDate, List<IrCurveHis> iRateBaseList, List<Hw1fCalibParas> hwParasList, int[] alphaPiece, int[] sigmaPiece, String mode, boolean priceAdj,
			                 int scenNum, char cmpdType, boolean isRealNumber, double ltfr, int ltfrT, int prjYear, int dayCountBasis) {				
		super();		
		this.baseDate = baseDate;
		this.isRealNumber = isRealNumber;
		this.setTermStructureBase(iRateBaseList);
		
		Arrays.sort(alphaPiece);
		Arrays.sort(sigmaPiece);		
		this.alphaPiece = alphaPiece;
		this.sigmaPiece = sigmaPiece;
		this.mode       = mode.toUpperCase(); 
		this.priceAdj   = priceAdj;
		
		this.scenNum = scenNum;
		this.cmpdType = cmpdType;
		
		this.ltfr = ltfr;
		this.ltfrT = ltfrT;
		this.prjYear = prjYear;		
		this.dayCountBasis = dayCountBasis;
		this.dt = 1.0 / 12;
		this.applySmithWilsonInterpoloation();
		this.setHwParasList(hwParasList);
	}	
	
	public Hw1fSimulationKics(String bssd, List<IrCurveHis> iRateBaseList, List<Hw1fCalibParas> hwParasList, int[] alphaPiece, int[] sigmaPiece, String mode, boolean priceAdj, int scenNum, double ltfr, int ltfrT, int prjYear) {				
		super();		
		this.baseDate = DateUtil.convertFrom(bssd);
		this.isRealNumber = true;
		this.setTermStructureBase(iRateBaseList);
		
		Arrays.sort(alphaPiece);
		Arrays.sort(sigmaPiece);		
		this.alphaPiece = alphaPiece;
		this.sigmaPiece = sigmaPiece;
		this.mode       = mode.toUpperCase(); 
		this.priceAdj   = priceAdj;
		
		this.scenNum = scenNum;
		this.cmpdType = IrmodelHw.CMPD_MTD_DISC;		
		
		this.ltfr = ltfr;
		this.ltfrT = ltfrT;
		this.prjYear = prjYear;		
		this.dayCountBasis = IrmodelHw.DCB_MON_DIF;
		this.dt = 1.0 / 12;
		this.applySmithWilsonInterpoloation();
		this.setHwParasList(hwParasList);
	}	

	
	public List<IrModelSce> getIrModelHw1fList() {
		
		this.calHw1fTheta();
		this.randomNumberGaussian();
		
		if(this.priceAdj) this.calShortRateAdj();
		else this.calShortRate();
		
		this.calHw1fTermStructure();
		this.checkMartingaleTest();
	
		List<IrModelSce> sceRslt = new ArrayList<IrModelSce>();		
		
		for(int i=0; i<this.prjMonth; i++) {
			for(int j=0; j<this.scenNum; j++) {
				
				IrModelSce sce = new IrModelSce();
				
				sce.setBaseDate(dateToString(this.baseDate));
				sce.setIrModelId("HW1F");
				sce.setMatCd(String.format("%s%04d", TIME_UNIT_MONTH, (i+1)));
				sce.setSceNo(String.valueOf(j+1));
				sce.setIrCurveNm("RF_KRW");
				sce.setMonthSeq(i+1);
				sce.setSpotRateDisc(this.spotDiscScen  [i][j]);			
				sce.setSpotRateCont(this.spotContScen  [i][j]);
				sce.setFwdRateDisc (this.fwdDiscScen   [i][j]);			
				sce.setFwdRateCont (this.fwdContScen   [i][j]);
				sce.setDcntFactor  (this.dcntFactorScen[i][j]);
				sce.setTheta       (this.theta         [i]);
				
				sceRslt.add(sce);				
			}
		}	
		return sceRslt;
	}	

	
	
	public List<IrModelBondYield> getIrModelHw1fBondYield(List<IrModelSce> hw1fRslt, double bondDuraYear) {
		
		if(hw1fRslt == null || hw1fRslt.isEmpty()) hw1fRslt = this.getIrModelHw1fList();		
		this.duraMonth = (int) Math.round(bondDuraYear * MONTH_IN_YEAR);		
		
		this.calBondYield();
		
		List<IrModelBondYield> yieldRslt = new ArrayList<IrModelBondYield>();		
		
		for(int i=0; i<this.prjMonth; i++) {
			for(int j=0; j<this.scenNum; j++) {
				
				IrModelBondYield yield = new IrModelBondYield();
				
				yield.setBaseDate(dateToString(this.baseDate));
				yield.setIrModelId("HW1F");
				yield.setMatCd(String.format("%s%04d", TIME_UNIT_MONTH, (i+1)));
				yield.setSceNo(String.valueOf(j+1));
				yield.setIrCurveId("RF_KRW");
				yield.setMonthSeq(i+1);
				yield.setBondYieldCont(this.bondYieldCont[i][j]);			
				yield.setBondYieldDisc(this.bondYieldDisc[i][j]);
				
				yieldRslt.add(yield);				
			}
		}	
		return yieldRslt;	
	}

	
	private void applySmithWilsonInterpoloation() {
		
		Map<Double, Double> ts = new TreeMap<Double, Double>();
		for(int i=0; i<this.tenor.length; i++) ts.put(this.tenor[i], this.iRateBase[i]);
		
		SmithWilsonKics sw  = new SmithWilsonKics(this.baseDate, ts, this.cmpdType, this.isRealNumber, this.ltfr, this.ltfrT, this.prjYear + 1, this.dayCountBasis);		
		this.swRsltList = sw.getSmithWilsonResultList();	
		
		this.monthSeq       = this.swRsltList.stream().map(s -> Integer.parseInt(s.getMatCd().substring(1, 5))).mapToInt(Integer::intValue).toArray();
		this.timeFactor     = this.swRsltList.stream().map(s -> s.getMatTerm()).mapToDouble(Double::doubleValue).toArray();
		this.matCd          = this.swRsltList.stream().map(s -> s.getMatCd()).toArray(String[]::new);		
		this.spotContBase   = this.swRsltList.stream().map(s -> s.getSpotCont()).mapToDouble(Double::doubleValue).toArray();
		this.spotDiscBase   = this.swRsltList.stream().map(s -> s.getSpotDisc()).mapToDouble(Double::doubleValue).toArray();
		this.fwdContBase    = this.swRsltList.stream().map(s -> s.getFwdCont()).mapToDouble(Double::doubleValue).toArray();
		this.fwdDiscBase    = this.swRsltList.stream().map(s -> s.getFwdDisc()).mapToDouble(Double::doubleValue).toArray();
		this.dcntFactorBase = this.swRsltList.stream().map(s -> s.getDcntFactor()).mapToDouble(Double::doubleValue).toArray();
		
//		swRslt.stream().filter(s-> Double.parseDouble(s.getMatCd().substring(1, 5)) % 12 == 0 && Double.parseDouble(s.getMatCd().substring(1, 5)) <= 240
//		                       ||  Double.parseDouble(s.getMatCd().substring(1, 5)) >= 1199)
//		               .forEach(s->log.info("{}, {}, {}, {}, {}, {}, {}, {}, {}", 
//		                        s.getBaseDate(), s.getResultType(), s.getMatCd(), s.getSpotCont(), s.getSpotDisc(), s.getDcntFactor(), s.getFwdCont(), s.getFwdDisc(), s.getMatTerm()));		
//		for(int i=0; i<this.monthSeq.length; i++) log.info("{}, {}, {}, {}, {}, {}", this.monthSeq[i], this.matCd[i], this.spotContBase[i], this.dcntFactorBase[i], this.fwdContBase[i], this.timeFactor[i]);
	}
	
	
	private void setHwParasList(List<Hw1fCalibParas> hwParasList) {		

		this.alpha = new double[this.monthSeq.length];
		this.sigma = new double[this.monthSeq.length];

		for(Hw1fCalibParas paras : hwParasList) {
			this.alphaMap.put(paras.getMatCd(), paras.getAlpha());
			this.sigmaMap.put(paras.getMatCd(), paras.getSigma());
		}
		
		for(int i=0; i<this.monthSeq.length; i++) {
			
			for(Map.Entry<String, Double> alpha : this.alphaMap.entrySet()) {
				if(compareDbltoInt(this.monthSeq[i], Integer.valueOf(alpha.getKey().substring(1, 5))) < 1) {
					this.alpha[i] = alpha.getValue();
					break;
				}
				this.alpha[i] = this.alphaMap.lastEntry().getValue();
			}
			
			for(Map.Entry<String, Double> sigma : this.sigmaMap.entrySet()) {
				if(compareDbltoInt(this.monthSeq[i], Integer.valueOf(sigma.getKey().substring(1, 5))) < 1) {
					this.sigma[i] = sigma.getValue();
					break;
				}
				this.sigma[i] = this.sigmaMap.lastEntry().getValue();
			}				
		}	
//		for(int i=0; i<this.monthSeq.length; i++) if(i<240 && (i+1) % 12 == 0) log.info("{}, {}, {}", i+1, this.alpha[i], this.sigma[i]);
	}	
	
	
	private void calHw1fTheta() {
		
		this.prjMonth = this.prjYear * MONTH_IN_YEAR;
		this.theta    = new double[this.prjMonth];

		for(int i=0; i<this.prjMonth; i++) {
			this.theta[i] = (this.fwdContBase[i+1] - this.fwdContBase[i]) / (this.alpha[i] * dt) + this.fwdContBase[i] + this.coefZeta(i);
//			log.info("Theta: {}, Alpha: {}, Sigma: {}, FWD_i+1: {} FWD_i: {}, tf: {}", this.theta[i], this.alpha[i], this.sigma[i], this.fwdContBase[i+1], this.fwdContBase[i], this.timeFactor[i]);
		}
	}	
	
	
	private double coefZeta(int monIdx) {
		return coefZeta(monIdx, this.mode);
	}
	
	
	private double coefZeta(int monIdx, String piece) {		
		
		if(piece.equals("SIGMA")) {
			return coefZetaSigmaPiece(monIdx);
		}
		else if(piece.equals("DUAL")) {
			return coefZetaDualPiece(monIdx);			
		}
		else return coefZetaConst(monIdx);
	}

//	1 / a * [Integral sigma(s)^2 * exp(-2 * a * (t1-s)) * ds] for Calculation Theta(t)
	private double coefZetaSigmaPiece(int monIdx) {		

		double zeta = 0.0;
		
		for(int i=0; i<=monIdx; i++) {				
			double timeFactor_0 = ((i==0) ? 0 : this.timeFactor[i-1]);			
			zeta += Math.pow(this.sigma[i], 2) * ( Math.exp(2*this.alpha[i]*this.timeFactor[i]) - Math.exp(2*this.alpha[i]*timeFactor_0) ) / (2 * this.alpha[i]);
		}
		zeta  = zeta * Math.exp(-2*this.alpha[monIdx]*this.timeFactor[monIdx]) / this.alpha[monIdx];		
//		if(monIdx <= 24) log.info("monIdx: {}, zeta: {}, tf: {}, alpha: {}, sigma: {}", monIdx+1, zeta, this.timeFactor[monIdx], alpha[monIdx], sigma[monIdx]);

		return zeta;
	}	
	
//	1 / a(t) * [Integral sigma(s)^2 * exp(-2 * a(s) * (t1-s)) * ds] for Calculation Theta(t)	
	private double coefZetaDualPiece(int monIdx) {
		
		int tauMonth = this.alphaPiece[0] * MONTH_IN_YEAR - 1;
		if(monIdx <= tauMonth) return coefZetaSigmaPiece(monIdx);
		
		double zeta = 0.0;		

		for(int i=0; i<=tauMonth; i++) {				
			double timeFactor_0 = ((i==0) ? 0 : this.timeFactor[i-1]);			
			zeta += Math.pow(this.sigma[i], 2) * ( Math.exp(2*this.alpha[i]*this.timeFactor[i]) - Math.exp(2*this.alpha[i]*timeFactor_0) ) / (2 * this.alpha[i])
				             * Math.exp( -2*(this.alpha[tauMonth] - this.alpha[monIdx])*this.timeFactor[tauMonth] );
		}
		
		for(int i=tauMonth+1; i<=monIdx; i++) {	
			zeta += Math.pow(this.sigma[i], 2) * ( Math.exp(2*this.alpha[i]*this.timeFactor[i]) - Math.exp(2*this.alpha[i]*this.timeFactor[i-1]) ) / (2 * this.alpha[i]);
		}						
		zeta  = zeta * Math.exp(-2*this.alpha[monIdx]*this.timeFactor[monIdx]) / this.alpha[monIdx];		
//		log.info("monIdx: {}, tauMonth: {}, timeFactor_monIdx:{}, timeFactor_tau: {}, alpha_monIdx: {}, alpha_tau: {}", monIdx, tauMonth, round(this.timeFactor[monIdx], 1), this.timeFactor[tauMonth], this.alpha[monIdx], this.alpha[tauMonth]);
		
		return zeta;		
	}

//	1 / a * [Integral sigma^2 * exp(-2 * a * (t1-s)) * ds] for Calculation Theta(t)
	private double coefZetaConst(int monIdx) {		
		return (  Math.pow(this.sigma[monIdx], 2) / (2 * Math.pow(this.alpha[monIdx], 2)) * (1.0 - Math.exp(-2 * this.alpha[monIdx] * this.timeFactor[monIdx])) );
	}
	
	
	private void calShortRate() {
		
		this.sRateScen = new double[this.prjMonth][this.scenNum];
		this.sRateMean = new double[this.prjMonth];
		
		for(int j=0; j<this.scenNum; j++) this.sRateScen[0][j] = this.fwdContBase[0];
		
		for(int i=0; i<this.prjMonth-1; i++) {
			for(int j=0; j<this.scenNum; j++) {
				this.sRateScen[i+1][j] = this.sRateScen[i][j] + this.alpha[i] * (this.theta[i] - this.sRateScen[i][j]) * this.dt + this.sigma[i] * Math.sqrt(this.dt)* this.randNum[i][j] * 1.0;
			}
		}		
		this.sRateMean = matToVecMean(this.sRateScen);
	}


	private void calShortRateAdj() {
		
		this.sRateScen = new double[this.prjMonth][this.scenNum];
		this.sRateMean = new double[this.prjMonth];
		double[][] dcntFactorProb  = new double[this.prjMonth][this.scenNum];
		
		for(int j=0; j<this.scenNum; j++) {
			this.sRateScen[0][j] = this.fwdContBase[0];
			dcntFactorProb[0][j] = 1.0 * Math.exp(-this.sRateScen[0][j] * this.dt);  
		}
		
		for(int i=0; i<this.prjMonth-1; i++) {
			double probDf    = 0.0;
			double detmDf    = 0.0;
			double sRateMean = 0.0;
			double sRateAdj  = 0.0;	
			
			//TODO:
			for(int j=0; j<this.scenNum; j++) {
				this.sRateScen[i+1][j] = this.sRateScen[i][j] + this.alpha[i] * (this.theta[i] - this.sRateScen[i][j]) * this.dt + this.sigma[i] * Math.sqrt(this.dt)* this.randNum[i][j] * 1.0;
//				limit value of sqrt( {1 - exp(-2*a*dt)} / (2*a) ) is sqrt(dt) when a -> 0 
//				this.sRateScen[i+1][j] = this.sRateScen[i][j] + this.alpha[i] * (this.theta[i] - this.sRateScen[i][j]) * this.dt 
//						               + this.sigma[i] * Math.sqrt( (1-Math.exp(-2*this.alpha[i]*this.dt)) / (2*this.alpha[i]) ) * this.randNum[i][j] * 1.0;
				dcntFactorProb[i+1][j] = dcntFactorProb[i][j] * Math.exp(-this.sRateScen[i+1][j] * dt);
				probDf                += dcntFactorProb[i+1][j];
				sRateMean             += this.sRateScen[i+1][j];
			}
			probDf    = (probDf > ZERO_DOUBLE ? probDf : 1) / (this.scenNum > 0 ? this.scenNum :  1);
			detmDf    = this.dcntFactorBase[i+1];
			sRateMean = sRateMean / (this.scenNum > 0 ? this.scenNum :  1);
			sRateAdj  = -Math.log(detmDf / probDf) / this.dt;

			for(int j=0; j<this.scenNum; j++) {
				this.sRateScen[i+1][j] = this.sRateScen[i+1][j] + sRateAdj * 1.0;
				dcntFactorProb[i+1][j] = dcntFactorProb[i][j] * Math.exp(-this.sRateScen[i+1][j] * dt);				
			}
			
			double sRateMax = -1.0;
			double sRateMin =  1.0;
			
			for(int j=0; j<this.scenNum; j++) {
				if(sRateMax < this.sRateScen[i+1][j]) sRateMax = this.sRateScen[i+1][j];
				if(sRateMin > this.sRateScen[i+1][j]) sRateMin = this.sRateScen[i+1][j];
			}
			if(i<2 || i>1190) log.info("idx: {}, sRateMeanOrg: {}, sRateAdj: {}, sRateMax: {}, sRateMin: {}, probDf: {}, detmDf: {}, probDfAdj: {}", i+1, sRateMean, sRateAdj, sRateMax, sRateMin, probDf, detmDf, probDf * Math.exp(-sRateAdj * this.dt));			
		}		
		this.sRateMean = matToVecMean(this.sRateScen);				
	}	
	
	
	private void calHw1fTermStructure() {
		
		this.dcntFactorScen = new double[this.prjMonth][this.scenNum];
		this.spotContScen   = new double[this.prjMonth][this.scenNum];
		this.spotDiscScen   = new double[this.prjMonth][this.scenNum];
		this.fwdContScen    = new double[this.prjMonth][this.scenNum];
		this.fwdDiscScen    = new double[this.prjMonth][this.scenNum];	
		
		for(int j=0; j<this.scenNum; j++) {
			this.dcntFactorScen[0][j] =  1.0 * Math.exp(-this.sRateScen[0][j] * this.dt);                     //  Df[t0,] = 1 (i.e. P(0,0)), this.dcntFactorScen[-1][j] = 1.0
			this.spotContScen  [0][j] = -1.0 * Math.log(this.dcntFactorScen[0][j]) / this.timeFactor[0];
			this.spotDiscScen  [0][j] = irContToDisc(this.spotContScen[0][j]);
			this.fwdContScen   [0][j] = this.spotContScen[0][j];
			this.fwdDiscScen   [0][j] = irContToDisc(this.fwdContScen[0][j]);
		}
		
		for(int i=0; i<this.prjMonth-1; i++) {
			for(int j=0; j<this.scenNum; j++) {
				this.dcntFactorScen[i+1][j] = this.dcntFactorScen[i][j] * Math.exp(-this.sRateScen[i+1][j] * this.dt);
				this.spotContScen  [i+1][j] = -1.0 * Math.log(this.dcntFactorScen[i+1][j]) / this.timeFactor[i+1];
				this.spotDiscScen  [i+1][j] = irContToDisc(this.spotContScen[i+1][j]);
				this.fwdContScen   [i+1][j] = (this.spotContScen[i+1][j] * this.timeFactor[i+1] - this.spotContScen[i][j] * this.timeFactor[i]) / this.dt;
				this.fwdDiscScen   [i+1][j] = irContToDisc(this.fwdContScen[i+1][j]);
			}			
		}
		
		this.dcntFactorMean = matToVecMean(this.dcntFactorScen);
		this.spotContMean   = matToVecMean(this.spotContScen  );
		this.spotDiscMean   = matToVecMean(this.spotDiscScen  );
		this.fwdContMean    = matToVecMean(this.fwdContScen   );
		this.fwdDiscMean    = matToVecMean(this.fwdDiscScen   );		
	}	
	
	//check forward rate for validation of HW1F result with Excel Validation File   
	private void checkMartingaleTest() {
		
		double[] fwdDisc = new double[this.dcntFactorMean.length];		
		
		fwdDisc[0] = Math.pow(1.0 / this.dcntFactorMean[0], 12) - 1.0;
		log.info("idx: {}, fwdDiscMean: {}, fwdDiscFromDf: {}, fwdDiscBase: {}", 0, this.fwdDiscMean[0], fwdDisc[0], this.fwdDiscBase[0]);
		
		for(int i=0; i<this.prjMonth-1; i++) {			
			fwdDisc[i+1] = Math.pow(this.dcntFactorMean[i] / this.dcntFactorMean[i+1], 12) - 1.0;
			if(i<2 || i>1196) log.info("idx: {}, fwdDiscMean: {}, fwdDiscFromDf: {}, fwdDiscBase: {}", i+1, this.fwdDiscMean[i+1], fwdDisc[i+1], this.fwdDiscBase[i+1]);
		}		
	}	
	
	//monthly bond yield from HW1F: [ Exp(fwdContBase*dt) - 1.0 or (1+fwdDiscBase)^(dt) - 1.0 ] 
	private void calBondYield() {
		
		this.bondYieldCont = new double[this.prjMonth][this.scenNum];
		this.bondYieldDisc = new double[this.prjMonth][this.scenNum];
		
		for(int i=0; i<this.prjMonth; i++) {
			for(int j=0; j<this.scenNum; j++) {				
				this.bondYieldCont[i][j] = this.fwdContBase[i] * this.dt - this.sigma[i] * this.coefB( (i+1)*this.dt, (i+1+this.duraMonth)*this.dt ) * Math.sqrt(this.dt) * this.randNum[i][j];
				this.bondYieldDisc[i][j] = irContToDisc(this.bondYieldCont[i][j]);
			}
		}		
//		log.info("{}", this.bondYieldCont);
		
		this.bondYieldContMean = matToVecMean(this.bondYieldCont);
		this.bondYieldDiscMean = matToVecMean(this.bondYieldDisc);

	}
	

	private double coefB(double t1, double t2) {		
		
		int tau        = this.alphaPiece[0];
		double[] alpha = new double[] {this.alpha[0], this.alpha[tau*MONTH_IN_YEAR]};
		
//		double t2 is less than or equal to integer tau, then return -1 or 0
		if(compareDbltoInt(t2,  tau) < 1) {
			return 1/alpha[0] * (1 - Math.exp(-alpha[0]*(t2 - t1)));			
		}
//		double t1 is greater than integer tau then return 1
		else if(compareDbltoInt(t1,  tau) > 0) {
			return 1/alpha[1] * (1 - Math.exp(-alpha[1]*(t2 - t1)));
		}
//		tau is in between t1 and t2
		else {
			return 1/alpha[0] * (1 - Math.exp(-alpha[0]*(tau - t1))) + Math.exp(-alpha[0]*(tau - t1)) / alpha[1] * (1 - Math.exp(-alpha[1]*(t2 - tau)));
		}		
	}
	
	private void randomNumberGaussian() {				
//		randLinearCongruential(Integer.valueOf(dateToString(this.baseDate.minusDays(0))));		
		mersenneTwister(Integer.valueOf(dateToString(this.baseDate.minusDays(0))));
//		log.info("{}, {}, {}, {}, {}, {}", this.randNum[0][0], this.randNum[0][this.scenNum-1], this.randNum[1][0], this.randNum[this.prjMonth-1][0], this.randNum[this.prjMonth-1][this.scenNum-1]);
	}
	
	
	protected void randLinearCongruential(int seed) {		

		double[] randNum = randLinearCongruentDbl((long) Math.pow(2, 31), 65539, 0, seed, this.prjMonth * this.scenNum);              // for cross-check with R or Excel (considering integer calculation accuracy)
//		double[] randNum = randLinearCongruentDbl((long) Math.pow(2, 31), 1103515245, 12345, 800000, this.prjMonth * this.scenNum);   // for more accurate method
		
		double[] randNumInvCdf = randNumInvCdf(randNum);
		log.info("Rands' Mean: {}, Rands' Sd: {}", vectMean(randNumInvCdf), vectSd(randNumInvCdf));
		
		this.randNum = new double[this.prjMonth][this.scenNum];
		
		for(int i=0; i<this.prjMonth; i++) {
			for(int j=0; j<this.scenNum; j++) {
				this.randNum[i][j] = randNumInvCdf[this.scenNum*i + j];
			}				
		}
	}
	

	protected void mersenneTwister(int seed) {		
				
		GaussianRandomGenerator grg = new GaussianRandomGenerator(new MersenneTwister(Long.valueOf(seed)));
		
		int scenNumGen = (this.scenNum + 1) / 2;
		this.randNum   = new double[this.prjMonth][this.scenNum];
		
		for(int i=0; i<this.prjMonth; i++) {
			for(int j=0; j<scenNumGen; j++) {			
				
				double random        = grg.nextNormalizedDouble();				
				this.randNum[i][j*2] = +random;
				if(this.scenNum > j*2 + 1) this.randNum[i][j*2 + 1] = -random;
			}
		}		
	}
	
}	