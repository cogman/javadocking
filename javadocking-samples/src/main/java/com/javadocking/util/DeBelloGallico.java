package com.javadocking.util;


public class DeBelloGallico {

	public static final Liber LIBER1 = new Liber("Liber 1");
	public static final Liber LIBER2 = new Liber("Liber 2");
	public static final Liber LIBER3 = new Liber("Liber 3");
	public static final Liber LIBER4 = new Liber("Liber 4");
	public static final Liber LIBER5 = new Liber("Liber 5");
	public static final Liber LIBER6 = new Liber("Liber 6");
	public static final Liber LIBER7 = new Liber("Liber 7");
	public static final Liber LIBER8 = new Liber("Liber 8");
	private static final String NEWLINE = System.getProperty("line.separator");

	public static String getText(Liber book) {

		if (book == LIBER1) {
			return "Gallia est omnis divisa in partes tres, quarum unam incolunt Belgae, " +
					"aliam Aquitani, tertiam qui ipsorum lingua Celtae, nostra Galli " +
					"appellantur. " + NEWLINE +
					"Hi omnes lingua, institutis, legibus inter se differunt." + NEWLINE +
					"Gallos ab Aquitanis Garumna flumen, a Belgis Matrona et Sequana " +
					"dividit." + NEWLINE +
					"Horum omnium fortissimi sunt Belgae, propterea quod a cultu atque " +
					"humanitate provinciae longissime absunt, minimeque ad eos mercatores " +
					"saepe commeant atque ea quae ad effeminandos animos pertinent " +
					"important, proximique sunt Germanis, qui trans Rhenum incolunt, " +
					"quibuscum continenter bellum gerunt." + NEWLINE +
					"Qua de causa Helvetii quoque reliquos Gallos virtute praecedunt, " +
					"quod fere cotidianis proeliis cum Germanis contendunt, cum aut suis " +
					"finibus eos prohibent aut ipsi in eorum finibus bellum gerunt." + NEWLINE +
					"Eorum una pars, quam Gallos obtinere dictum est, initium capit a " +
					"flumine Rhodano, continetur Garumna flumine, Oceano, finibus Belgarum, " +
					"attingit etiam ab Sequanis et Helvetiis flumen Rhenum, vergit ad " +
					"septentriones." + NEWLINE + "Belgae ab extremis Galliae finibus " +
					"oriuntur, pertinent ad inferiorem partem fluminis Rheni, spectant in " +
					"septentrionem et orientem solem." + NEWLINE + "Aquitania a Garumna " +
					"flumine ad Pyrenaeos montes et eam partem Oceani quae est ad Hispaniam " +
					"pertinet; spectat inter occasum solis et septentriones.";
		}
		if (book == LIBER2) {
			return "Cum esset Caesar in citeriore Gallia [in hibernis], ita uti supra " +
					"demonstravimus, crebri ad eum rumores adferebantur litterisque item Labieni " +
					"certior fiebat omnes Belgas, quam tertiam esse Galliae partem dixeramus, " +
					"contra populum Romanum coniurare obsidesque inter se dare." + NEWLINE +
					"Coniurandi has esse causas: primum quod vererentur ne, omni pacata Gallia, ad " +
					"eos exercitus noster adduceretur; deinde quod ab non nullis Gallis " +
					"sollicitarentur, partim qui, ut Germanos diutius in Gallia versari noluerant, " +
					"ita populi Romani exercitum hiemare atque inveterascere in Gallia moleste " +
					"ferebant, partim qui mobilitate et levitate animi novis imperiis studebant; " + NEWLINE +
					"ab non nullis etiam quod in Gallia a potentioribus atque iis qui ad conducendos " +
					"homines facultates habebant vulgo regna occupabantur; qui minus facile eam rem " +
					"imperio nostro consequi poterant.";
		}
		if (book == LIBER3) {
			return "Cum in Italiam proficisceretur Caesar, Ser." + NEWLINE +
					"Galbam cum legione XII et parte equitatus in Nantuates, Veragros Sedunosque " +
					"misit, qui a finibus Allobrogum et lacu Lemanno et flumine Rhodano ad summas " +
					"Alpes pertinent." + NEWLINE + "Causa mittendi fuit quod iter per Alpes, quo " +
					"magno cum periculo magnisque cum portoriis mercatores ire consuerant, " +
					"patefieri volebat." + NEWLINE +
					"Huic permisit, si opus esse arbitraretur, uti in his locis legionem hiemandi " +
					"causa conlocaret." + NEWLINE + "Galba secundis aliquot proeliis factis " +
					"castellisque compluribus eorum expugnatis, missis ad eum undique legatis " +
					"obsidibusque datis et pace facta, constituit cohortes duas in Nantuatibus " +
					"conlocare et ipse cum reliquis eius legionis cohortibus in vico Veragrorum, " +
					"qui appellatur Octodurus hiemare; qui vicus positus in valle non magna adiecta " +
					"planitie altissimis montibus undique continetur." + NEWLINE +
					"Cum hic in duas partes flumine divideretur, alteram partem eius vici Gallis " +
					"[ad hiemandum] concessit, alteram vacuam ab his relictam cohortibus attribuit." + NEWLINE +
					"Eum locum vallo fossaque munivit.";
		}
		if (book == LIBER4) {
			return "Ea quae secuta est hieme, qui fuit annus Cn." + NEWLINE +
					"Pompeio, M. Crasso consulibus, Usipetes Germani et item Tencteri magna [cum] " +
					"multitudine hominum flumen Rhenum transierunt, non longe a mari, quo Rhenus " +
					"influit. Causa transeundi fuit quod ab Suebis complures annos exagitati bello " +
					"premebantur et agri cultura prohibebantur. Sueborum gens est longe maxima et " +
					"bellicosissima Germanorum omnium." + NEWLINE +
					"Hi centum pagos habere dicuntur, ex quibus quotannis singula milia armatorum " +
					"bellandi causa ex finibus educunt." + NEWLINE + "Reliqui, qui domi manserunt, " +
					"se atque illos alunt; hi rursus in vicem anno post in armis sunt, illi domi " +
					"remanent." + NEWLINE +
					"Sic neque agri cultura nec ratio atque usus belli intermittitur." + NEWLINE +
					"Sed privati ac separati agri apud eos nihil est, neque longius anno remanere " +
					"uno in loco colendi causa licet." + NEWLINE +
					"Neque multum frumento, sed maximam partem lacte atque pecore vivunt; multum " +
					"sunt in venationibus; quae res et cibi genere et cotidiana exercitatione et " +
					"libertate vitae, quod a pueris nullo officio aut disciplina adsuefacti nihil " +
					"omnino contra voluntatem faciunt, et vires alit et immani corporum magnitudine " +
					"homines efficit." + NEWLINE +
					"Atque in eam se consuetudinem adduxerunt ut locis frigidissimis neque vestitus " +
					"praeter pelles habeant quicquam, quarum propter exiguitatem magna est corporis " +
					"pars aperta, et laventur in fluminibus.";
		}
		if (book == LIBER5) {
			return "L.Domitio Ap. Claudio consulibus, discedens ab hibernis Caesar in " +
					"Italiam, ut quotannis facere consuerat, legatis imperat quos legionibus " +
					"praefecerat uti quam plurimas possent hieme naves aedificandas veteresque " +
					"reficiendas curarent." + NEWLINE +
					"Earum modum formamque demonstrat." + NEWLINE +
					"Ad celeritatem onerandi subductionesque paulo facit humiliores quam quibus in " +
					"nostro mari uti consuevimus, atque id eo magis, quod propter crebras " + NEWLINE +
					"commutationes aestuum minus magnos ibi fluctus fieri cognoverat; ad onera, " +
					"ad multitudinem iumentorum transportandam paulo latiores quam quibus in " +
					"reliquis utimur maribus." + NEWLINE +
					"Has omnes actuarias imperat fieri, quam ad rem multum humilitas adiuvat." + NEWLINE +
					"Ea quae sunt usui ad armandas naves ex Hispania apportari iubet." + NEWLINE +
					"Ipse conventibus Galliae citerioris peractis in Illyricum proficiscitur, quod " +
					"a Pirustis finitimam partem provinciae incursionibus vastari audiebat." + NEWLINE +
					"Eo cum venisset, civitatibus milites imperat certumque in locum convenire iubet." + NEWLINE +
					"Qua re nuntiata Pirustae legatos ad eum mittunt qui doceant nihil earum rerum " +
					"publico factum consilio, seseque paratos esse demonstrant omnibus rationibus " +
					"de iniuriis satisfacere." + NEWLINE +
					"Accepta oratione eorum Caesar obsides imperat eosque ad certam diem adduci " +
					"iubet; nisi ita fecerint, sese bello civitatem persecuturum demonstrat." + NEWLINE +
					"Eis ad diem adductis, ut imperaverat, arbitros inter civitates dat qui litem " +
					"aestiment poenamque constituant.";
		}
		if (book == LIBER6) {
			return "Multis de causis Caesar maiorem Galliae motum exspectans per Marcum " +
					"Silanum, Gaium Antistium Reginum, Titum Sextium legatos dilectum habere " +
					"instituit; simul ab Gnaeo Pompeio proconsule petit, quoniam ipse ad urbem " +
					"cum imperio rei publicae causa remaneret, quos ex Cisalpina Gallia consulis " +
					"sacramento rogavisset, ad signa convenire et ad se proficisci iuberet, magni " +
					"interesse etiam in reliquum tempus ad opinionem Galliae existimans tantas " +
					"videri Italiae facultates ut, si quid esset in bello detrimenti acceptum, non " +
					"modo id brevi tempore sarciri, sed etiam maioribus augeri copiis posset." + NEWLINE +
					"Quod cum Pompeius et rei publicae et amicitiae tribuisset, celeriter confecto " +
					"per suos dilectu tribus ante exactam hiemem et constitutis et adductis " +
					"legionibus duplicatoque earum cohortium numero, quas cum Quinto Titurio " +
					"amiserat, et celeritate et copiis docuit, quid populi Romani disciplina " +
					"atque opes possent.";
		}
		if (book == LIBER7) {
			return "Quieta Gallia Caesar, ut constituerat, in Italiam ad conventus agendos " +
					"proficiscitur." + NEWLINE +
					"Ibi cognoscit de Clodii caede [de] senatusque consulto certior factus, ut " +
					"omnes iuniores Italiae coniurarent, delectum tota provincia habere instituit." + NEWLINE +
					"Eae res in Galliam Transalpinam celeriter perferuntur." + NEWLINE +
					"Addunt ipsi et adfingunt rumoribus Galli, quod res poscere videbatur, " +
					"retineri urbano motu Caesarem neque in tantis dissensionibus ad exercitum " +
					"venire posse." + NEWLINE + "Hac impulsi occasione, qui iam ante se populi " +
					"Romani imperio subiectos dolerent liberius atque audacius de bello consilia " +
					"inire incipiunt." + NEWLINE + "Indictis inter se principes Galliae conciliis " +
					"silvestribus ac remotis locis queruntur de Acconis morte; posse hunc casum ad " +
					"ipsos recidere demonstrant; miserantur communem Galliae fortunam: omnibus " +
					"pollicitationibus ac praemiis deposcunt qui belli initium faciant et sui " +
					"capitis periculo Galliam in libertatem vindicent." + NEWLINE + "In primis " +
					"rationem esse habendam dicunt, priusquam eorum clandestina consilia " +
					"efferantur, ut Caesar ab exercitu intercludatur." + NEWLINE + "Id esse facile, " +
					"quod neque legiones audeant absente imperatore ex hibernis egredi, neque " +
					"imperator sine praesidio ad legiones pervenire possit." + NEWLINE +
					"Postremo in acie praestare interfici quam non veterem belli gloriam " +
					"libertatemque quam a maioribus acceperint recuperare.";
		}
		if (book == LIBER8) {
			return "Omni Gallia devicta Caesar cum a superiore aestate nullum bellandi " +
					"tempus intermisisset militesque hibernorum quiete reficere a tantis laboribus " +
					"vellet, complures eodem tempore civitates renovare belli consilia nuntiabantur " +
					"coniurationesque facere." + NEWLINE + "Cuius rei verisimilis causa adferebatur, " +
					"quod Gallis omnibus cognitum esset neque ulla multitudine in unum locum coacta " +
					"resisti posse Romanis, nec, si diversa bella complures eodem tempore " +
					"intulissent civitates, satis auxili aut spati aut copiarum habiturum exercitum " +
					"populi Romani ad omnia persequenda; non esse autem alicui civitati sortem " +
					"incommodi recusandam, si tali mora reliquae possent se vindicare in libertatem.";
		}

		return null;
	}


	public static class Liber {
		private String text;

		private Liber(String text) {
			this.text = text;
		}

		public String getTitle() {
			return text;
		}
	}


//	public DeBelloGallico()
//	{
//		try
//		{
//			Properties properties = PropertiesUtil.loadProperties("D://projects/sanaware/samples/com/javadocking/resources/debellogallico.txt");
//			System.out.println("OK");
//			String liber1 = properties.getProperty("liber1");
//			System.out.println(liber1);
//		}
//		catch (IOException exception)
//		{
//			exception.printStackTrace();
//		}
////		Properties properties = PropertiesUtil.loadProperties(getClass().getResource("/com/javadocking/resources/images/text12.gif"));
//	}
//	
//	public static void main(String[] args)
//	{
//		new DeBelloGallico();
//	}

}
