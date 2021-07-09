package com.gof.process;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.gof.dao.DcntRateDao;
import com.gof.dao.EsgMetaDao;
import com.gof.dao.EsgParamDao;
import com.gof.dao.IrCurveHisDao;
import com.gof.dao.IrCurveYtmHisDao;
import com.gof.dao.SwaptionVolDao;
import com.gof.entity.DcntRateBiz;
import com.gof.entity.DcntSceBiz;
import com.gof.entity.EsgMeta;
import com.gof.entity.EsgMst;
import com.gof.entity.EsgParamBiz;
import com.gof.entity.IrCurve;
import com.gof.entity.IrCurveHis;
import com.gof.entity.IrCurveYtmHis;
import com.gof.entity.JobLog;
import com.gof.entity.SwaptionVol;
import com.gof.enums.EEsgCalibModel;
import com.gof.enums.EIrModelType;
import com.gof.enums.EJob;
import com.gof.enums.ELiqModel;
import com.gof.enums.ERunArgument;
import com.gof.enums.ERunSettings;
import com.gof.infra.EntityManagerUtil;
import com.gof.model.hw.Hw1fCalibParas;
import com.gof.model.hw.Hw1fSimulationKics;
import com.gof.util.DateUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * ESG Main Process.
 * @author takion77@gofconsulting.co.kr
 * @version 1.0
 *
 */
@Slf4j
public class Main {
	private static EntityManager em = EntityManagerUtil.getEntityManger();
	
	private static Map<ERunArgument, String> argInputMap = new HashMap<>();
	private static double kicsVolAdjust 	= 0.0032;

	private static double dnsErrorTolerance = 0.00001;
	private static double hwErrorTolerance  = 0.00000001;
	private static double hw2ErrorTolerance = 0.00000001;
	private static String irSceGenSmithWilsonApply = "Y";
	
	private static long cnt = 0;
	private static long totalSize = 0;
	private static int 	projectionYear=100;
	
	private static String paramGroup ;
	private static String jobString;
	private static String irSceCurrencyString;
//	private static String lqFittingModel;
	
	
	private static String bssd;
	private static int poolSize;
	private static ExecutorService exe ;
	private static Session session;
	private static int flushSize 				= 10000; 
//	private static double decayFactor 			= 0.97;
//	private static String ewmaYn 				="Y";
//	private static int volDataSize 				=250;
//	private static String volCalcId 		 	="SMA";			//Defalut Vol Calc Id
//	private static String localVolYn 			= "Y";
//	private static double targetDuration	 = 3.0;
	
//	private static int flushSize = 50;
	
	private static Map<String, IrCurve> rfCurveMap 		= new HashMap<String, IrCurve>();
	private static Map<String, IrCurve> bottomUpMap 	= new HashMap<String, IrCurve>();
	private static Map<String, IrCurve> kicsMap 		= new HashMap<String, IrCurve>();
	
//	private static List<IrCurve> rfCurveList 	= new ArrayList<IrCurve>();
//	private static List<IrCurve> bottomUpList 	= new ArrayList<IrCurve>();
//	private static List<IrCurve> kicsList 		= new ArrayList<IrCurve>(); 
	
	private static List<String> jobList 		= new ArrayList<String>();
	private static Set<String> irSceCurrency 	= new HashSet<>();
	private static List<String> irSceCurrencyList 	= new ArrayList<String>();
	
// ******************************************************************For AFNS ********************************	
	
//	private static String stBssd     	 = "20100101";
//	private static String mode            = "DNS";	
	
//	private static String irCurveId       = "111111C";
//	private static String baseCurveId     = "111111C";
//	private static String realNumberStr   = "N"; 
//	private static boolean isRealNumber   = false;
//	private static String cmpdTypeStr     = "DISC";
//	private static char   cmpdType        = 'D';
//	private static double dt              = 1.0 / 52.0;
//	private static String tenorListStr    = "M0003";
//	private static List<String> tenorList = new ArrayList<String>();
	
//	private static double errorTolerance  = 0.0000000001;
//	private static int    kalmanItrMax    = 100;
//	private static double confInterval    = 0.995;
//	private static double sigmaInit       = 0.05;
//	private static double lambdaMin       = 0.05;
//	private static double lambdaMax       = 2.00;
//	private static double epsilonInit     = 0.001;
//	private static int    dayCountBasis   = 1;
	
	private static int    prjYear         = 140;
//	private static double ltfrL           = 0.045;
//	private static double ltfrA           = 0.045;
//	private static int    ltfrT           = 60;	
//	private static double liqPrem         = 0.0032;	
	private static double[] afnsInitalParam ;
	private static double[] afnsInitalLcs	;
	
// *********************************************************************************************************	
	
	
	
	public static void main(String[] args) {
// ******************************************************************Run Argument &  Common Data Setting ********************************
		init(args);		// Input Data Setting
// ******************************************************************Pre Validation ********************************
//		job1();			// Validation

// ******************************************************************ESG Stock Parameter Job ********************************
		job10();        // Job10 : IrCurveHis from RF_YTM(KOFIA) with Smith Wilson
		job10A();       // Job10A: IrCurveHis from RF_YTM(KOFIA) with Smith Wilson for all Date
		job11();		// Job11 : ESG Parameter  : Vasicek, CIR, HULL AND WHITE, Hull and White 2 Factor
//		job12();		// Job1w : ESG Parameter  : Vasicek, CIR, HULL AND WHITE, Hull and White 2 Factor
		job13();		// job13 : Biz ESG Param
//		
////****************************************************************** LIQ PREMIUM & TERM STRUCTURE *******************************
		job15();		// job 15: Avg Liq Prem & Eom Liq Prem ( new!!!!)
		job15A();		// job 15: Eom Liq Prem for avg Num( new!!!!)
		job16();		// job 16: Biz Liq prem
		job17();		// job 17: Bottom Up Curve : Risk Adj Bottomup ( add liq prem )
		job18();		// job 18: Biz Bottom Up Curve for ifrs with Smith Wilson
//		job19();		// job 19: Biz IrCurve His with Smith Wilson
//		
//// ****************************************************************** Bottom Up Scenario Job *********************************************************
//		job24A();		// job 24 : Biz Bottom Up Scenario Async Mode IFRS 
//		job24B();		// job 24 : Biz Bottom Up Scenario Async Mode KICS
//		job25A();		// job 25 : Biz Bottom Up Scenario Async Mode IFRS_JAVA
//		job25B();		// job 25 : Biz Bottom Up Scenario Async Mode KICS_JAVA
//		
////		20210621 Add
		job25C();		// job 25 : Biz Bottom Up Scenario Async Mode IFRS_JAVA_NEW
		job25D();		// job 25 : Biz Bottom Up Scenario Async Mode KICS_JAVA_NEW
		
		job27();		// job 27 : Int Rate Week for AFNS
		job28();		// job 28 : AFNS at DB
		job29();		// Job 29 : KICS AFNS as HK
		
// ******************************************************************ESG Stock Simulation Job ********************************		
		
//		job31();		// Random Number under Correlation
//		job32();		// Random Number for Hull White
//		
//		job34();		// Std Asset Yield Deterministic Scenario from irCurve
//		job34A();		// Std Asset Yield Deterministic Scenario from irCurve_JAVA
//		
//		job35();		// Std Asset Yield Scenaio for KOSPI200_IFRS
//		job36();		// Std Asset Yield Scenaio for KOSPI200_KICS
//		
//		job38();		// Std Asset Yield (Bond Yield) Scenaio_Hull White4j_IFRS
//		job39();		// Std Asset Yield (Bond Yield) Scenaio_Hull White4j_KICS
		
// ******************************************************************ESG Ir Simulation Job ********************************
//		
//		job41();		// job41 : Ir Scenario from bottomUp IFRS
//		job42();		// job42 : Ir Scenario from bottomUp KICS
//		job43();		// job43 : HULL WHITE Scenario DB
		
//		job48();		// job48 : DNS Scenario TODO
		

// ****************************************************************** 공시이율 Job **********************************************************

//		job51();		// job51 : 내부모형 기준의 자산운용수익률, 외부금리, 공시이율  통계분석 결과 산출( 통계산출 불가능한 이율코드임.: 데이터 부족 등등)
//		job52();		// job52 : 산출한 통계모형 또는 사용자가 입력한 통계모형 중 적용할 통계모형을 결정하며, 현재 공시이율 수준 Fitting 작업을 수행함. 
//		job53();		// Job53 : Forward Rate Generaion for IFRS
//		job54();		// Job54 : IFRS 공시이율 
//		job56();		// Job56 : IFRS 공시이율 시나리오   

// ****************************************************************** 공시이율 Job KICS**********************************************************		
		
//		job61();		// Job61 : 감독원 기준의 통계모형 산출   
//		job62();		// Job62 : 사용자가 입력한 통계모형으로 변환 & 현재 공시이율 수준 Fitting 작업을 수행함. (구분자 K : No Fitting,   S : Fitting)
//		job63();		// Job63 : Forward Rate Generaion for KICS
//		job64();		// Job64 : KICS 공시이율 
//		
//		job65();		// Job65 : KICS 공시이율 시나리오
		
// ****************************************************************** Inflation ********************************		
//		job71();		// Job 71 : CPI Inflation !!!!
//		job72();		// Job 72 : BIZ INFLATION IFRS!!!
//		job73();		// Job 73 : BIZ INFLATION KICS!!!

// ****************************************************************** RC Job ********************************
//		job81();		// Job 81 : TM
//		job82();		// Job 82 : IFRS Corp PD
//		job83();		// Job 83 : KICS Corp PD
//		
//		job86();		// Job 86 : LGD
//		job87();		// Job 87 : IFRS 17 LGD
//		job88();		// Job 88 : KIcS LGD

// *********************************************************** End Job ********************************
//		HibernateUtil.shutdown();
		
		System.exit(1);
	}

	private static void init(String[] args) {
		for (String aa : args) {
			log.info("Input Arguments : {}", aa);
			for (ERunArgument bb : ERunArgument.values()) {
				if (aa.split("=")[0].toLowerCase().contains(bb.name())) {
					argInputMap.put(bb, aa.split("=")[1]);
					break;
				}
			}
		}
		argInputMap.entrySet().forEach(s-> log.info("aaaa :  {},{}", s.getKey(), s.getValue()));
		
		try {
			bssd       = argInputMap.get(ERunArgument.time).replace("-", "").replace("/", "").substring(0, 6);
			paramGroup = argInputMap.get(ERunArgument.meta).trim();
			jobString  = argInputMap.get(ERunArgument.job);
			
		} catch (Exception e) {
			log.error("Argument error : -Dtime, -Dmeta, -Djob" );
			System.exit(0);
		}
		
		EsgMetaDao.getEsgMeta(paramGroup).forEach(s-> log.info("Run Settings in DB :  {},{}", s.getParamKey(), s.getParamValue()));
		Map<String, EsgMeta> runSettingMap = EsgMetaDao.getEsgMeta(paramGroup).stream().collect(toMap(s->s.getParamKey(), Function.identity()));
		
		
		for(ERunSettings aa : ERunSettings.values()) {
			if(runSettingMap.containsKey(aa.name())) {
				aa.setValue(runSettingMap.get(aa.name()).getParamValue());
			}
		}
		Arrays.stream(ERunSettings.values()).forEach(s-> log.info("Applie Run Setting in Enum : {},{}", s.name(), s.getValue()));
		
		
		if(jobString == null || jobString.isEmpty()) {
			if(!ERunSettings.JOB.getValue().isBlank()) {
				jobString = ERunSettings.JOB.getValue();
			}
			else {
				log.error("There are no jobs in the input argument and ESG_META Table");
				System.exit(1);
			}
		}
		jobList = Arrays.stream(jobString .split(",")).map(s -> s.trim()).collect(Collectors.toList());
//		jobList.forEach(s-> log.info("job list 1:  {},{}", s));
		
		irSceCurrencyList = ERunSettings.IR_CURVE_CURRENCY.getStringList();
		List<IrCurve> curveList = IrCurveHisDao.getRiskFreeIrCurve().stream().filter(s ->irSceCurrencyList.contains(s.getCurCd())).collect(toList());
		
		
		rfCurveMap  = curveList.stream().filter(s -> s.getRefCurveNm()==null || "".equals(s.getRefCurveNm().trim())).collect(toMap(s->s.getCurCd(), Function.identity(), (s,t)-> s));
//		rfCurveMap  = curveList.stream().filter(s -> s.getRefCurveId()==null ).collect(toMap(s->s.getCurCd(), Function.identity(), (s,t)-> s));
		bottomUpMap = curveList.stream().filter(s -> "I".equals(s.getApplBizDv()) && s.getRefCurveNm()!=null).collect(toMap(s->s.getCurCd(), Function.identity(), (s,t)-> s));
		kicsMap     = curveList.stream().filter(s -> "K".equals(s.getApplBizDv()) && s.getRefCurveNm()!=null).collect(toMap(s->s.getCurCd(), Function.identity(), (s,t)-> s));
		
		
		session = em.unwrap(Session.class);
	}

	private static void job10() {
		if (jobList.contains("10")) {
			Transaction tx = session.beginTransaction();
//			EntityTransaction tx = em.getTransaction();
			JobLog jobLog = startJogLog(EJob.ESG11A);			
			
			try {
				String rfKrwNm 			= ERunSettings.ESG_RF_KRW_ID.getStringValue();
				double spotSwAlpha		= ERunSettings.SPOT_SW_ALPHA.getDoubleValue();
				double spotSwLiqPrem	= ERunSettings.SPOT_SW_LIQ_PREM.getDoubleValue();
				int spotSwLlp			= ERunSettings.SPOT_SW_LLP.getIntValue();
				int spotSwFreq			= ERunSettings.SPOT_SW_FREQ.getIntValue();
				
				String maxBaseDate = IrCurveYtmHisDao.getMaxBaseDate(bssd, rfKrwNm);
				int deleteNum = em.createQuery("delete IrCurveHis a where a.baseDate=:param and a.irCurveNm=:param2")
							.setParameter("param", maxBaseDate)
							.setParameter("param2", rfKrwNm)
							.executeUpdate();
				
				log.info("delete from IrCurveHis :  {},{},{}", rfKrwNm, maxBaseDate, deleteNum);
				
				List<IrCurveYtmHis> ytmRst = IrCurveYtmHisDao.getIrCurveYtmHis(bssd, rfKrwNm);			
				
				if(ytmRst.size()==0) {
					log.error("No YTM Curve His Data exist for {}", bssd);
					System.exit(0);
				}				
				
//				Job11A_IrCurveYtmHis.createIrCurveHis(maxBaseDate, rfKrwId, ytmRst, true).forEach(s -> session.saveOrUpdate(s));
				Job11A_IrCurveYtmHis.createIrCurveHis(maxBaseDate, rfKrwNm, ytmRst, spotSwAlpha, (double)spotSwLlp, spotSwFreq, spotSwLiqPrem).forEach(s -> em.persist(s));
				
				em.flush();
				
				completJob("SUCCESS", jobLog);
				
			} catch (Exception e) {
				log.error("error : {}", e);
				completJob("ERROR", jobLog);
				System.exit(0);
			}
			
			em.persist(jobLog);
			tx.commit();			
		}
	}
	
	private static void job10A() {
		if (jobList.contains("10A")) {
			Transaction tx = session.beginTransaction();
			LocalDate baseDate = DateUtil.convertFrom("20210706");

			for(int i=0; i< 3000; i++) {
				
//			EntityTransaction tx = em.getTransaction();
			JobLog jobLog = startJogLog(EJob.ESG11A);			
			
			try {
//				String rfKrwNm = EsgConstant.getStrConstant().get("ESG_RF_KRW_ID");
				String rfKrwNm = ERunSettings.ESG_RF_KRW_ID.getStringValue();
//				String baseDate = argInputMap.get(ERunArgument.time);
				LocalDate date = baseDate.minusDays(i);
				
				double spotSwAlpha		= ERunSettings.SPOT_SW_ALPHA.getDoubleValue();
				double spotSwLlp		= ERunSettings.SPOT_SW_LLP.getDoubleValue();
				double spotSwLiqPrem	= ERunSettings.SPOT_SW_LIQ_PREM.getDoubleValue();
				int spotSwFreq			= ERunSettings.SPOT_SW_FREQ.getIntValue();
				String zzzz = date.format(DateTimeFormatter.BASIC_ISO_DATE);
				
				int deleteNum = em.createQuery("delete IrCurveHis a where a.baseDate=:param and a.irCurveNm=:param2")
							.setParameter("param", zzzz)
							.setParameter("param2", rfKrwNm)
							.executeUpdate();
				
				log.info("delete from IrCurveHis :  {},{},{},{}", rfKrwNm, baseDate, deleteNum, zzzz);
				
				List<IrCurveYtmHis> ytmRst = IrCurveYtmHisDao.getIrCurveYtmHisAt(zzzz, rfKrwNm);			
				
				if(ytmRst.size()==0) {
					log.error("No YTM Curve His Data exist for {}", bssd);
//					System.exit(0);
					continue;
				}				
				
//				Job11A_IrCurveYtmHis.createIrCurveHis(maxBaseDate, rfKrwId, ytmRst, true).forEach(s -> session.saveOrUpdate(s));
				Job11A_IrCurveYtmHis.createIrCurveHis(zzzz, rfKrwNm, ytmRst, spotSwAlpha, spotSwLlp, spotSwFreq, spotSwLiqPrem).forEach(s -> em.persist(s));
				
				em.flush();
				
				completJob("SUCCESS", jobLog);
				
			} catch (Exception e) {
				log.error("error : {}", e);
				completJob("ERROR", jobLog);
				System.exit(0);
			}
			
			em.persist(jobLog);
			}
			tx.commit();			
		}
	}
	
	private static void job11() {
		if (jobList.contains("11")) {
			Transaction tx = session.beginTransaction();
//			EntityTransaction tx = em.getTransaction();
			JobLog jobLog = startJogLog(EJob.ESG11);
			try {
				String rfKrwNm = ERunSettings.ESG_RF_KRW_ID.getStringValue();
				
				int deleteNum = em.createQuery("delete EsgParamHis a where a.baseYymm=:param ")
						.setParameter("param", bssd)
						.executeUpdate();
				
				log.info("delete from EsgParamHis :  {},{},{}", bssd, deleteNum);
				
				List<String> tenorList = ERunSettings.TENOR_LIST.getStringList();
				
				double ufr  = ERunSettings.UFR_MAP.getDoubleMap().get("KRW");
				double ufrt = ERunSettings.UFRT_MAP.getIntMap().get("KRW");
				
				log.info("SmithWilson Param : {},{},{}", rfKrwNm, ufr, ufrt, tenorList.toString());
				
				List<SwaptionVol> volRst = SwaptionVolDao.getSwaptionVol(bssd);
				Map<String, Double> nullLpMap = new HashMap<String, Double>();
				List<IrCurveHis> curveRst = IrCurveHisDao.getIrCurveHis(bssd, rfKrwNm);
//				List<IrCurveYtmHis> ytmRst = IrCurveYtmHisDao.getIrCurveYtmHis(bssd, rfKrwId);
				
				if(volRst.size()==0 ) {
					log.error("Error in Loading Swaption Vol");
					System.exit(0);
				}else if(curveRst.size()==0 ) {
					log.error("Error in Loading IntRate Curve");
					System.exit(0);
				}
				List<EsgMst> esgMstList = em.createQuery("from EsgMst", EsgMst.class).getResultList();
				
				for(EsgMst mst : esgMstList) {
//					TODO : other Model Addddd :ASIS, HW, Sigma local calib ( ENUM....)
					if(EIrModelType.HW.equals(mst.getIrModelTyp()) && EEsgCalibModel.SIGMA_LOCAL.equals(mst.getCalibModel())) {
						Job11_EsgParameter.createHwKicsParamCalcHisAsync(bssd, curveRst, volRst, ufr, ufrt, 1e-10, mst).forEach(s->em.merge(s));
					}
				}
				
				
				em.flush();
				
				completJob("SUCCESS", jobLog);
				
			} catch (Exception e) {
				log.info("eee : {}", e);
				completJob("ERROR", jobLog);
				System.exit(0);
			}
			
			em.merge(jobLog);
			tx.commit();
		}
	}
	
	private static void job13() {
		if (jobList.contains("13")) {
			Transaction tx = session.beginTransaction();
			JobLog jobLog = startJogLog(EJob.ESG13);
			try {
				int deleteNum = em.createQuery("delete EsgParamBiz a where a.baseYymm=:param").setParameter("param", bssd).executeUpdate();
				log.info("delete from BizEsgParam :  {},{},{}", bssd, deleteNum);
				
				String ifrsIrModelId = ERunSettings.IFRS_IR_MODEL_ID.getStringValue();
				String kicsIrModelId = ERunSettings.KICS_IR_MODEL_ID.getStringValue();
				log.info("irModel Id :  {}, {}", ifrsIrModelId, kicsIrModelId);
				
				Job13_EsgParamBiz.createBizAppliedParameter(bssd, "I", ifrsIrModelId).stream().forEach(s -> em.persist(s));
				Job13_EsgParamBiz.createBizAppliedParameter(bssd, "K", kicsIrModelId).stream().forEach(s -> em.persist(s));
				
				completJob("SUCCESS", jobLog);
			} catch (Exception e) {
				log.error("error : {}", e);
				completJob("ERROR", jobLog);
				System.exit(0);
			}
			
			em.persist(jobLog);
			tx.commit();
		}	
	}
	
	private static void job15() {
		if (jobList.contains("15")) {
			Transaction tx = session.beginTransaction();
			
			JobLog jobLog = startJogLog(EJob.ESG15);
			try {
				int deleteNum = em.createQuery("delete LiqPremHis a where a.baseYymm=:param").setParameter("param", bssd).executeUpdate();
				log.info("delete from LiqPremHis :  {},{},{}", bssd, deleteNum);
				
				
//				String ifrsliqModelNm =  ERunSettings.IFRS_LIQ_MODEL_NM.getStringValue();
//				Job15_LiquidPremium.createLiquidPremiumEom(bssd, ifrsliqModelNm).stream().forEach(s -> em.persist(s));
//				Job15_LiquidPremium.createLiquidPremium(bssd, kicsliqModelNm).stream().forEach(s -> session.saveOrUpdate(s));
				
				for(ELiqModel model : ELiqModel.values()) {
					Job15_LiquidPremium.createLiquidPremiumEom(bssd, model).stream().forEach(s -> em.persist(s));
				}
				
//				String kicsliqModelNm = ERunSettings.KICS_LIQ_MODEL_NM.getStringValue();
//				String kicsVolAddjId = "KICS_VOL_ADJ";
				
//				Job15_LiquidPremium.createLiquidPremiumFrom(bssd, kicsliqModelNm, kicsVolAdjust).stream().forEach(s -> em.persist(s));
				completJob("SUCCESS", jobLog);
				
			} catch (Exception e) {
				log.error("ERROR : {}", e);
				completJob("ERROR", jobLog);
				System.exit(0);
			}
			
			em.persist(jobLog);
			tx.commit();
		}	
	}
	
	
	private static void job15A() {
		if (jobList.contains("15A")) {
			Transaction tx = session.beginTransaction();
			
			JobLog jobLog = startJogLog(EJob.ESG15A);
			try {
				int avgNum = ERunSettings.LIQ_AVG_NUM.getIntValue();
				
				for(int i = avgNum+1 ; i <= 0 ; i++) {
					String tempBssd = DateUtil.addMonthToString(bssd, i);
					
					int deleteNum = em.createQuery("delete LiqPremHis a where a.baseYymm=:param").setParameter("param", tempBssd).executeUpdate();
					log.info("delete from LiqPremHis :  {},{},{}", tempBssd, deleteNum);
					
					
					for(ELiqModel model : ELiqModel.values()) {
						Job15_LiquidPremium.createLiquidPremiumEom(tempBssd, model).stream().forEach(s -> em.persist(s));
					}
				}

				completJob("SUCCESS", jobLog);
				
			} catch (Exception e) {
				log.error("ERROR : {}", e);
				completJob("ERROR", jobLog);
				System.exit(0);
			}
			
			em.persist(jobLog);
			tx.commit();
		}	
	}
	private static void job16() {
		if (jobList.contains("16")) {
			Transaction tx = session.beginTransaction();
			JobLog jobLog = startJogLog(EJob.ESG16);
			try {
				int deleteNum = em.createQuery("delete LiqPremBiz a where a.baseYymm=:param").setParameter("param", bssd).executeUpdate();
				log.info("delete from LiqPremBiz :  {},{},{}", bssd, deleteNum);
				
				
				String ifrsLiqModelNm = ERunSettings.IFRS_LIQ_MODEL_NM.getStringValue();
				String kicsLiqModelNm = ERunSettings.KICS_LIQ_MODEL_NM.getStringValue();
				
				int ifrsLiqAvgNum = ERunSettings.LIQ_AVG_NUM.getIntValue();
				int kicsLiqAvgNum = ERunSettings.LIQ_AVG_NUM.getIntValue();
				
				
				log.info("aaa : {},{}", ifrsLiqModelNm, kicsLiqModelNm);
				
				Job16_LiqPremBiz.createBizLiqPremium(bssd, "I", ifrsLiqModelNm, ifrsLiqAvgNum).stream().forEach(s -> em.persist(s));
				Job16_LiqPremBiz.createBizLiqPremium(bssd, "K", kicsLiqModelNm, kicsLiqAvgNum).stream().forEach(s -> em.persist(s));
				
				completJob("SUCCESS", jobLog);
			} catch (Exception e) {
				log.error("error : {}", e);
				completJob("ERROR", jobLog);
				System.exit(0);
			}
			
			em.persist(jobLog);
			tx.commit();
		}	
	}
	
	private static void job17() {
		if (jobList.contains("17")) {
			Transaction tx = session.beginTransaction();
			JobLog jobLog = startJogLog(EJob.ESG17);
			try {
				int deleteNum = em.createQuery("delete DcntRateBu a where a.baseYymm=:param").setParameter("param", bssd).executeUpdate();
				log.info("delete from BottomupDcnt :  {},{},{}", bssd, deleteNum);
				
				
				String ifrsLiqModelNm = ERunSettings.IFRS_LIQ_MODEL_NM.getStringValue();  
				String kicsLiqModelNm = ERunSettings.IFRS_LIQ_MODEL_NM.getStringValue();  
//				String lqModelId = ParamUtil.getParamMap().getOrDefault("lqModelId", "COVERED_BOND_KDB");
//				String kicsVolAddjId = "KICS_VOL_ADJ";
				
				log.info("aaaa : {},{}", kicsMap.size(), bottomUpMap.size()); 
				
				kicsMap.values().stream().flatMap(s-> Job17_BottomUp.createBottomUpAddLiqPremium(bssd, s, kicsLiqModelNm)).forEach(s -> em.persist(s));

				bottomUpMap.values().stream().flatMap(s-> Job17_BottomUp.createBottomUpAddLiqPremium(bssd, s, ifrsLiqModelNm)).forEach(s->em.persist(s));
				
				completJob("SUCCESS", jobLog);
			} catch (Exception e) {
				log.error("error : {}", e);
				completJob("ERROR", jobLog);
				System.exit(0);
			}
			
			em.persist(jobLog);
			tx.commit();
		}	
	}
	
	
	private static void job18() {
		if (jobList.contains("18")) {
			Transaction tx = session.beginTransaction();
			JobLog jobLog = startJogLog(EJob.ESG18);
			try {
				int deleteNum = em.createQuery("delete DcntRateBiz a where a.baseYymm=:param").setParameter("param", bssd).executeUpdate();
				log.info("delete from DcntRateBiz :  {},{},{}", bssd, deleteNum);
				
				
				bottomUpMap.values().stream().flatMap(s-> Job18_DcntRateBiz.createBizBottomUpCurve(bssd, s)).forEach(s-> em.persist(s));
				
				kicsMap.values().stream().flatMap(s-> Job18_DcntRateBiz.createBizBottomUpCurve(bssd, s)).forEach(s-> em.persist(s));
			
				
				completJob("SUCCESS", jobLog);
			} catch (Exception e) {
				log.error("error : {}", e);
				completJob("ERROR", jobLog);
				System.exit(0);
			}
			
			em.persist(jobLog);
			tx.commit();
		}	
	}
	
	private static void job25C() {
		if (jobList.contains("25C")) {
			Transaction tx = session.beginTransaction();
			JobLog jobLog = startJogLog(EJob.ESG25);
			try {
				
				String irModelId = ERunSettings.IFRS_IR_MODEL_ID.getStringValue();
				List<EsgParamBiz> bizParamHis = EsgParamDao.getBizEsgParam(bssd, irModelId);			
				if(bizParamHis.size()==0) {
					log.error("No ESG Parameter exist for Hull and White Model");
					System.exit(0);
				}

				String bizDv ="I";
				int deleteNum = em.createQuery("delete DcntSceBiz a where a.baseYymm=:param  and a.applBizDv=:bizDv")
									.setParameter("param", bssd)
									.setParameter("bizDv", bizDv)
									.executeUpdate();
				log.info("delete from DcntSceBiz :  {},{},{}", bssd, deleteNum);
				
				int sceNum = ERunSettings.BATCH_NUM.getIntValue() * 100 ;
				sceNum =10;		//TODO  : temp ..delete after test!!!
				
				for(IrCurve curve : bottomUpMap.values()) {	
					
					double ufr  = ERunSettings.UFR_MAP.getDoubleMap().get(curve.getCurCd());
					int    ufrt = ERunSettings.UFRT_MAP.getIntMap().get(curve.getCurCd());
					
					List<DcntSceBiz> rst = setupSce(bizDv, curve, sceNum, ufr, ufrt, bizParamHis);
					
					int sceCnt = 1;
					for (DcntSceBiz bb :rst) {
						em.persist(bb);
						if (sceCnt % 50 == 0) {
							em.flush();
							em.clear();
						}
						if (sceCnt % flushSize == 0) {
							log.info("Dcnt Rate Scenario for {}  is processed {}/{} in Job 25C {}", curve.getCurCd(),sceCnt, sceNum * projectionYear * 12);
						}
						sceCnt = sceCnt + 1;
					}
				}	
				
				completJob("SUCCESS", jobLog);
			} catch (Exception e) {
				log.error("error : {}", e);
				completJob("ERROR", jobLog);
				System.exit(0);
			}
			
			em.persist(jobLog);
			tx.commit();
		}	
	}

	private static void job25D() {
		if (jobList.contains("25D")) {
			Transaction tx = session.beginTransaction();
			JobLog jobLog = startJogLog(EJob.ESG26);
			try {
				
				String irModelId = ERunSettings.KICS_IR_MODEL_ID.getStringValue();
				List<EsgParamBiz> bizParamHis = EsgParamDao.getBizEsgParam(bssd, irModelId);			
				
				if(bizParamHis.size()==0) {
					log.error("No ESG Parameter exist for Hull and White Model");
					System.exit(0);
				}

				String bizDv ="K";
				int deleteNum = em.createQuery("delete DcntSceBiz a where a.baseYymm=:param  and a.applBizDv=:bizDv")
						.setParameter("param", bssd)
						.setParameter("bizDv", bizDv)
						.executeUpdate();
				log.info("delete from DcntSceBiz :  {},{},{}", bssd, deleteNum);
				
				int sceNum = ERunSettings.BATCH_NUM.getIntValue() * 100 ;
				sceNum =10;		//TODO  : temp ..delete after test!!!
				
				for(IrCurve curve : kicsMap.values()) {	
					log.info("Dcnt Rate Scenario for {} : {}", curve.getCurCd(), curve.getIrCurveNm(), curve.getIrCurveName());
					
					double ufr  = ERunSettings.UFR_MAP.getDoubleMap().get(curve.getCurCd());
					int    ufrt = ERunSettings.UFRT_MAP.getIntMap().get(curve.getCurCd());
					
					List<DcntSceBiz> rst = setupSce(bizDv, curve, sceNum, ufr, ufrt, bizParamHis);
					
					int sceCnt = 1;
					for (DcntSceBiz bb :rst) {
						em.persist(bb);
						if (sceCnt % 50 == 0) {
							em.flush();
							em.clear();
						}
						if (sceCnt % flushSize == 0) {
							log.info("Dcnt Rate Scenario for {}  is processed {}/{} in Job 25C {}", curve.getCurCd(),sceCnt, sceNum * projectionYear * 12);
						}
						sceCnt = sceCnt + 1;
					}
			
				}	
				
				completJob("SUCCESS", jobLog);
			} catch (Exception e) {
				log.error("error : {}", e);
				completJob("ERROR", jobLog);
				System.exit(0);
			}	
			
			em.persist(jobLog);
			tx.commit();
		}	
	}
	
	private static void job27() {
		if (jobList.contains("27")) {
			Transaction tx = session.beginTransaction();
			JobLog jobLog = startJogLog(EJob.ESG27);
			String historyDataTerm		=   "WEEK"; 
			String historyDataFromYn	=	"Y";
			int	 	historyDataMonth	=	-120;
			String 	historyDataFrom		=	ERunSettings.AFNS_START_DATE.getStringValue();
			String shockModel			=	ERunSettings.AFNS_MODE.getStringValue();
			String shockCurveId			=	ERunSettings.AFNS_BASE_CURVE_ID.getStringValue();
			String shockStBssd ;
			
			if(historyDataFromYn.equals("Y")) {
				shockStBssd = historyDataFrom;
			}
			else {
				shockStBssd = DateUtil.addMonthToString(bssd, historyDataMonth);
			}
			List<String> afnsTenorList = ERunSettings.AFNS_TENOR_LIST.getStringList();
			
			try {
				
				String stBssd 	=  shockStBssd;
				String irCurveNm = shockCurveId;
				
				int deleteNum = session.createQuery("delete IrCurveWeek a where a.irCurveNm = :param and a.baseDate >= :param1 and a.baseDate <= :param2")
								.setParameter("param", irCurveNm)
								.setParameter("param1", stBssd)
								.setParameter("param2", bssd+"31").executeUpdate();
				
				log.info("Job 19(AFNS Setup Week IR Curve) delete {} rows in the db", deleteNum);

				Job27_AfnsScenarioDbSetup.setupIrCurveWeek(bssd, stBssd, irCurveNm, afnsTenorList, DayOfWeek.FRIDAY).forEach(s-> session.save(s));
				
				
				completJob("SUCCESS", jobLog);
			} catch (Exception e) {
				log.error("error : {}", e);
				completJob("ERROR", jobLog);
				System.exit(0);
			}	
			
			em.persist(jobLog);
			tx.commit();
		}	
	}
	private static void job28() {
		if (jobList.contains("28")) {
			Transaction tx = session.beginTransaction();
			JobLog jobLog = startJogLog(EJob.ESG28);
			try {
				
//				String stBssd 			= ERunSettings.AFNS_START_DATE.getStringValue();;
				String stBssd 			= "201601";
				String irCurveId 		= ERunSettings.AFNS_BASE_CURVE_ID.getStringValue();
				String baseCurveId     	= ERunSettings.AFNS_BASE_CURVE_ID.getStringValue();
				String mode            	= ERunSettings.AFNS_MODE.getStringValue();
				double ltfrL           	= ERunSettings.UFR_MAP.getDoubleMap().get("KRW");
				double ltfrA           	= ERunSettings.UFR_MAP.getDoubleMap().get("KRW");
				int    ltfrT           	= ERunSettings.UFRT_MAP.getIntMap().get("KRW");
				double liqPrem         	= 0.0;
				
				boolean isRealNumber   	= true;
				char   cmpdType        	= 'D';
//				double dt              	= 1.0/ ( historyDataTerm.equals("WEEK")? 52.0: 12.0 ); 
				double dt              	= 1.0/ 52.0; 
				
				double errorTolerance  	= 0.0000000001;
				int    kalmanItrMax    	= 100;
				double confInterval    	= 0.995;
				double sigmaInit       	= 0.05;
				double lambdaMin       	= 0.05;
				double lambdaMax       	= 2.00;
				double epsilonInit     	= 0.001;
				int    dayCountBasis   	= 1;
				int    prjYear         	= 140;
				
				log.info("input shock sce : {},{},{}", stBssd, irCurveId, mode);
				Map<String, List<?>> irShockSenario = new TreeMap<String, List<?>>();
				
				String[] tempStr = ERunSettings.AFNS_INIT_PARAM.getValue().split(",");
				String[] tempStr2 =ERunSettings.AFNS_INIT_LCS.getValue().split(",");
				
				if(tempStr.length==14 && tempStr2.length==3 ) {
					afnsInitalParam = new double[14];
					afnsInitalLcs = new double[3];
					
					for(int i=0; i< tempStr.length; i++) {
						afnsInitalParam[i] = (double)Double.valueOf(tempStr[i]);
						log.info("aaa : {},{}", i, afnsInitalParam[i]);
					}
					
					for(int i=0; i< tempStr2.length; i++) {
						afnsInitalLcs[i] = (double)Double.valueOf(tempStr2[i]);
						log.info("bbbb : {},{}", i, afnsInitalLcs[i]);
					}
				}
				List<String> afnsTenorList = ERunSettings.AFNS_TENOR_LIST.getStringList();
				
				irShockSenario = Job28_AfnsScenarioDb.createAfnsShockScenario(DateUtil.toEndOfMonth(bssd), mode, afnsInitalParam, afnsInitalLcs, irCurveId, baseCurveId, afnsTenorList, 
																			stBssd, isRealNumber, cmpdType, dt, sigmaInit, dayCountBasis,
																			ltfrL, ltfrA, ltfrT, liqPrem, lambdaMin, lambdaMax, 
																			prjYear, errorTolerance, kalmanItrMax, confInterval, epsilonInit);
				
				
				if(irShockSenario != null) {				
					for(Map.Entry<String, List<?>> rslt : irShockSenario.entrySet()) {
						rslt.getValue().forEach(s -> session.saveOrUpdate(s));
						
	//					if(!rslt.getKey().equals("CURVE")) rslt.getValue().forEach(s -> session.saveOrUpdate(s));
	//					rslt.getValue().forEach(s -> log.info("Arbitrage Free Nelson Siegle Scenario Result : {}", s.toString()));
					}
				}
				
				
				completJob("SUCCESS", jobLog);
			} catch (Exception e) {
				log.error("error : {}", e);
				completJob("ERROR", jobLog);
				System.exit(0);
			}	
			
			em.persist(jobLog);
			tx.commit();
		}	
	}
	
	private static void job29() {
		if (jobList.contains("29")) {
			Transaction tx = session.beginTransaction();
			JobLog jobLog = startJogLog(EJob.ESG29);
			try {
				int deleteNum = em.createQuery("delete IrShockSce a where a.baseDate=:param").setParameter("param", bssd).executeUpdate();
				log.info("delete from DcntRateBiz :  {},{},{}", bssd, deleteNum);
				
				
				Map<String, List<?>> irShockSenario = new TreeMap<String, List<?>>();
				String mode            = ERunSettings.AFNS_MODE.getStringValue();
				String stBssd          = ERunSettings.AFNS_START_DATE.getStringValue();
				String irCurveId       = ERunSettings.AFNS_HIST_CURVE_ID.getStringValue(); 
				String baseCurveId     = ERunSettings.AFNS_BASE_CURVE_ID.getStringValue();  
//				realNumberStr   = ERunSettings.AFNS_REAL_NUMBER_BL.getValue();
				boolean isRealNumber    = ERunSettings.AFNS_REAL_NUMBER_BL.getBooleanValue(); 

				String cmpdTypeStr     = ERunSettings.AFNS_CMPD_TYPE.getStringValue(); 
	            char   cmpdType        = (cmpdTypeStr.equals("CONT") ? 'C' : 'D');		
				double dt              = 1.0 / ERunSettings.AFNS_DT_DENOM.getDoubleValue();			
				
				double errorTolerance  = ERunSettings.AFNS_ERROR_TOL.getDoubleValue();
				int    kalmanItrMax    = ERunSettings.AFNS_KALMAN_ITR_MAX.getIntValue(); 
				double confInterval    = ERunSettings.AFNS_CONF_INTERVAL.getDoubleValue();
				double sigmaInit       = ERunSettings.AFNS_SIGMA_INIT.getDoubleValue(); 
				double lambdaMin       = ERunSettings.AFNS_LAMBDA_MIN.getDoubleValue();
				double lambdaMax       = ERunSettings.AFNS_LAMBDA_MAX.getDoubleValue();
				double epsilonInit     = ERunSettings.AFNS_EPSILON_INIT.getDoubleValue();
				int	   dayCountBasis   = ERunSettings.AFNS_DAY_COUNT_BASIS.getIntValue();			
				int    prjYear         = ERunSettings.AFNS_PROJ_YEAR.getIntValue(); 
				double ltfrL           = ERunSettings.AFNS_LTFR_INSU.getDoubleValue();
				double ltfrA           = ERunSettings.AFNS_LTFR_ASSET.getDoubleValue();
				int    ltfrT           = ERunSettings.AFNS_LTFR_TERM.getIntValue(); 
				double liqPrem         = ERunSettings.AFNS_KICS_VOL_ADJ.getDoubleValue();
				List<String> tenorList = ERunSettings.AFNS_TENOR_LIST.getStringList();		
				
				irShockSenario = Job29_AfnsScenario.createAfnsShockScenario(DateUtil.toEndOfMonth(bssd), mode, irCurveId, baseCurveId, tenorList, 
						                                                    stBssd, isRealNumber, cmpdType, dt, sigmaInit, dayCountBasis,
						                                                    ltfrL, ltfrA, ltfrT, liqPrem, lambdaMin, lambdaMax, 
						                                                    prjYear, errorTolerance, kalmanItrMax, confInterval, epsilonInit);
				
				if(irShockSenario != null) {				
					for(Map.Entry<String, List<?>> rslt : irShockSenario.entrySet()) {
						rslt.getValue().forEach(s -> em.merge(s));
					}
				}
				
				completJob("SUCCESS", jobLog);
			} catch (Exception e) {
				log.error("error : {}", e);
				completJob("ERROR", jobLog);
				System.exit(0);
			}
			
			em.persist(jobLog);
			tx.commit();
		}	
	}
	
	
	private static List<DcntSceBiz> setupSce(String bizDv, IrCurve curveMst, int sceNum, double ufr, int ufrt, List<EsgParamBiz> bizParamHis) {
			log.info("Dcnt Rate Scenario for {} : {}", curveMst.getCurCd(), curveMst.getIrCurveNm(), curveMst.getIrCurveName());
			
			List<DcntRateBiz> dcntRateHis = DcntRateDao.getTermStructure(bssd, curveMst.getApplBizDv(), curveMst.getIrCurveNm());
			if(dcntRateHis.size()==0) {
				log.error("No Ir Curve His Data exist for {}", bssd);
				System.exit(0);
			}
			
			List<IrCurveHis> irCurveHisList = dcntRateHis.stream().map(s->s.convert()).collect(toList());
			
			int[] alphaPiece = bizParamHis.stream().filter(s->s.getParamTypCd().equals("ALPHA")).mapToInt(s-> Integer.valueOf(s.getMatCd().split("M")[1])).toArray();
			int[] sigmaPiece = bizParamHis.stream().filter(s->s.getParamTypCd().equals("SIGMA")).mapToInt(s-> Integer.valueOf(s.getMatCd().split("M")[1])).toArray();
			
			
			String mode ="DUAL";
			boolean priceAdj =false;						
			
			List<Hw1fCalibParas> hwParasList = Hw1fCalibParas.convertFrom(bizParamHis);
			Hw1fSimulationKics hw4f = new Hw1fSimulationKics(bssd, irCurveHisList, hwParasList, alphaPiece, sigmaPiece, mode, priceAdj, sceNum, ufr, (int)ufrt, prjYear);
			
			List<DcntSceBiz> rst = hw4f.getIrModelHw1fList().stream().map(s->s.convert(bssd, bizDv)).collect(toList());
			return rst;
	}

	private static JobLog startJogLog(EJob job) {
		JobLog jobLog = new JobLog();
		jobLog.setJobStart(LocalDateTime.now());
		
		jobLog.setJobId(job.name());
		jobLog.setCalcStart(LocalDateTime.now().format(DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm:ss")));
		jobLog.setBaseYymm(bssd);
		jobLog.setCalcDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("YYYYMMdd")));
		jobLog.setJobNm(job.getJobName());
		
		log.info("{}({}) : Job Start !!! " , job.name(), job.getJobName());
		
		return jobLog;
	}
	
	private static void completJob(String successDiv, JobLog jobLog) {

		long timeElpse = Duration.between(jobLog.getJobStart(), LocalDateTime.now()).getSeconds();
		jobLog.setCalcEnd(LocalDateTime.now().format(DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm:ss")));
		jobLog.setCalcScd(successDiv);
		jobLog.setCalcElps(String.valueOf(timeElpse));
		
		log.info("{}({}): Job Completed with {} !!!!", jobLog.getJobId(), jobLog.getJobNm(),successDiv );
	}
}
