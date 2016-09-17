package com.infonapalm.ridbackend.Utils

/**
 * Created with IntelliJ IDEA.
 * User: infonapalm
 * Date: 10/3/15
 * Time: 6:26 PM

 */
object GeoConst {
  val CENTER_POINTS = List(
    "47.09537035351024,37.783355712890625",
    "47.1607737815166,37.731170654296875",
    "47.23728695323144,37.731170654296875",
    "47.32299967378833,37.750396728515625",
    "47.404855836246135,37.766876220703125",
    "47.48194469821279,37.698211669921875",
    "47.5561403129434,37.61993408203125",
    "47.61819841513311,37.527923583984375",
    "47.67648444221321,37.42767333984375",
    "47.746711194756,37.357635498046875",
    "47.82790816919327,37.33840942382812",
    "47.920943836444415,37.33154296875",
    "47.99267886541119,37.40570068359375",
    "48.0762438718672,37.467498779296875",
    "48.13768314385533,37.52105712890625",
    "48.21094727794909,37.577362060546875",
    "48.28593438872724,37.637786865234375",
    "48.357161569178395,37.692718505859375",
    "48.41006090395107,37.772369384765625",
    "48.46745824148332,37.85614013671875",
    "48.500227605781035,37.96051025390625",
    "48.53479452317522,38.06488037109375",
    "48.600225060468915,38.133544921875",
    "48.65287143545865,38.200836181640625",
    "48.693679928847146,38.28598022460937",
    "48.72449109301392,38.371124267578125",
    "48.748945343432936,38.463134765625",
    "48.75709411680183,38.57025146484374",
    "48.75981008089207,38.67187499999999",
    "48.76343113791796,38.770751953125",
    "48.77248263837077,38.8751220703125",
    "48.784247149826534,38.98361206054687",
    "48.815002837234,39.0838623046875",
    "48.84031689136024,39.17449951171874",
    "48.849354525964365,39.266510009765625",
    "48.84573966784039,39.34616088867187",
    "48.83941303819501,39.4573974609375",
    "48.783342285295475,39.539794921875",
    "48.746228791937774,39.620819091796875",
    "48.79148547876059,39.705963134765625",
    "48.86832824998009,39.711456298828125",
    "48.93873912193207,39.68536376953124",
    "49.01175312475694,39.67437744140625",
    "49.06486885623368,39.649658203125",
    "49.07026727876924,39.752655029296875",
    "49.0711669588361,39.84878540039062",
    "49.08915713896701,39.96551513671875",
    "49.08286131716694,40.060272216796875",
    "49.06216942501903,40.166015625",
    "49.049570140493145,40.296478271484375",
    "49.031565622700356,40.4241943359375",
    "49.007249184314254,40.513458251953125",
    "48.93873912193207,40.52032470703125",
    "48.86742490239797,40.510711669921875",
    "48.797818160096874,40.477752685546875",
    "48.72902055540814,40.48187255859375",
    "48.651964204615396,40.466766357421875",
    "48.58296689084418,40.4681396484375",
    "48.51205578443625,40.44891357421875",
    "48.434667703888834,40.4461669921875",
    "48.36628606659289,40.422821044921875",
    "48.30146673770983,40.43243408203125",
    "48.23382085308753,40.42144775390625",
    "48.16058943132621,40.41046142578124",
    "48.085418575511994,40.40496826171875",
    "48.011975126709956,40.37750244140625",
    "47.95038564051012,40.389862060546875",
    "47.87674969487649,40.373382568359375",
    "47.803008949806895,40.362396240234375",
    "47.73839980847376,40.356903076171875",
    "47.67925834499533,40.33355712890625",
    "47.615421267605434,40.301971435546875",
    "47.54872547286774,40.269012451171875",
    "47.48658499815577,40.23468017578124",
    "47.41972512929256,40.198974609375",
    "47.36208323188104,40.150909423828125",
    "47.31741394628156,40.06988525390625",
    "47.265252010946085,40.00946044921875",
    "47.2288945564232,39.93804931640625",
    "47.220500830563616,39.82543945312499",
    "47.2279619858493,39.70733642578125",
    "47.2288945564232,39.5782470703125",
    "47.25872815157009,39.453277587890625",
    "47.26897956964769,39.354400634765625",
    "47.28388717948357,39.25689697265625",
    "47.29879058949732,39.15115356445312",
    "47.266183925231,39.06463623046875",
    "47.22143353240336,38.9739990234375",
    "47.19624482325592,38.8751220703125",
    "47.17944570469834,38.733673095703125",
    "47.17104415159213,38.80645751953125",
    "47.14956747670931,38.640289306640625",
    "47.12621341795227,38.49334716796875",
    "47.15890622816097,38.557891845703125",
    "47.11313066447019,38.3917236328125",
    "47.09724013895744,38.272247314453125",
    "47.07199249565323,38.18572998046875",
    "47.03550255150042,38.09234619140625",
    "47.100044694025215,38.07586669921875",
    "47.09443543616595,37.97149658203125",
    "47.10845747289683,37.876739501953125",
    "48.452887283381344,40.078125",
    "48.99824008113872,39.79248046875",
    "49.01085236926211,39.92431640625",
    "49.01985919086641,40.02593994140625",
    "48.996438064932285,40.14129638671874",
    "48.98922934818294,40.2484130859375",
    "48.980216985374994,40.35552978515624",
    "48.942347261978476,40.42144775390625",
    "48.864714761802794,40.3857421875",
    "48.78153250728971,40.37200927734375",
    "48.68914728403741,40.37750244140625",
    "48.60567378414743,40.35003662109375",
    "48.53661318127801,40.341796875",
    "48.452887283381344,40.33355712890625",
    "48.381793961204984,40.32257080078125",
    "48.30329376225394,40.3033447265625",
    "48.21735290928554,40.2978515625",
    "48.1349337022896,40.30059814453125",
    "48.057889555610984,40.28961181640624",
    "47.97153658265933,40.27587890625",
    "47.90345483298757,40.26763916015625",
    "47.83159592699297,40.25665283203125",
    "47.76517619125415,40.25665283203125",
    "47.69497434186282,40.21545410156249",
    "47.619124098197325,40.16876220703124",
    "47.54872547286774,40.14404296875",
    "47.468949677672484,40.11383056640624",
    "47.40392636603371,40.06713867187499",
    "47.359292508710745,39.979248046875",
    "47.299721913179944,39.8968505859375",
    "47.297859249409825,39.78424072265625",
    "47.29413372501023,39.64691162109375",
    "47.301584511330795,39.53704833984374",
    "47.331377157798244,39.4134521484375",
    "47.34626718205302,39.30633544921875",
    "47.364873807434094,39.19647216796875",
    "47.349989032003215,39.06463623046875",
    "47.30530951077587,38.95751953125",
    "47.26991141830741,38.86962890625",
    "47.24381345414034,38.7762451171875",
    "47.24194882163242,38.65264892578125",
    "47.22143353240336,38.55926513671875",
    "47.18971246448421,38.4576416015625",
    "47.17104415159213,38.34503173828125",
    "47.16730970131578,38.2269287109375",
    "47.131819327544115,38.14727783203125",
    "47.17104415159213,38.04290771484375",
    "47.17664533468975,37.9248046875",
    "47.187845928576344,37.81768798828125",
    "47.26991141830741,37.83416748046875",
    "47.357431944587034,37.85614013671875",
    "47.429945332976125,37.880859375",
    "47.48380086737796,37.80120849609375",
    "47.55428670127958,37.72979736328125",
    "47.613569753973955,37.6885986328125",
    "47.646886969413,37.6171875",
    "47.69312564683551,37.52655029296875",
    "47.73932336136857,37.4688720703125",
    "47.79286140021344,37.43865966796875",
    "47.87767079094932,37.41943359375",
    "47.92738566360356,37.452392578125",
    "47.99727386804474,37.50732421875",
    "48.0762438718672,37.57598876953125",
    "48.145930585161196,37.63641357421875",
    "48.21735290928554,37.70507812499999",
    "48.281365151571755,37.760009765625",
    "48.34712273417819,37.79846191406249",
    "48.39091404578957,37.88909912109375",
    "48.427378042075105,37.97698974609375",
    "48.45835188280866,38.0621337890625",
    "48.48748647988415,38.1719970703125",
    "48.54752375797609,38.19122314453125",
    "48.596592251456705,38.2598876953125",
    "48.63290858589532,38.34503173828125",
    "48.66012869453836,38.4466552734375",
    "48.68189420361744,38.5565185546875",
    "48.68552087440201,38.66363525390625",
    "48.69096039092549,38.76525878906249",
    "48.70183766127341,38.86688232421875",
    "48.7127125814524,38.9739990234375",
    "48.75075629617738,39.07012939453125",
    "48.75981008089207,39.17999267578125",
    "48.776102781718585,39.28436279296875",
    "48.76886223397753,39.39697265624999",
    "48.721773219750666,39.46289062499999",
    "48.6927734325279,39.5562744140625",
    "48.67645370777654,39.6551513671875",
    "48.7181491602648,39.74578857421875",
    "48.777912755501845,39.80621337890624",
    "48.85206549830757,39.82269287109375",
    "48.929717630629554,39.79522705078125",
    "48.94054322456003,39.8858642578125",
    "48.94054322456003,39.98748779296875",
    "48.927913136732556,40.09735107421875",
    "48.92430395329745,40.1824951171875",
    "48.90444878143716,40.27313232421875",
    "48.83398957668602,40.2923583984375",
    "48.75075629617738,40.264892578125",
    "48.67101262432597,40.24566650390625",
    "48.60385760823255,40.23468017578124",
    "48.5275192374508,40.2264404296875",
    "48.45653041501911,40.22918701171875",
    "48.37449671682332,40.21270751953125",
    "48.295985271707664,40.1824951171875",
    "48.23747967660676,40.1934814453125",
    "48.167917284047974,40.20172119140625",
    "48.09275716032736,40.19622802734375",
    "48.021161285657804,40.1824951171875",
    "47.938426929481054,40.1605224609375",
    "47.864773955792245,40.1605224609375",
    "47.79286140021344,40.1495361328125",
    "47.730087095193056,40.13031005859375",
    "47.66168780332917,40.089111328125",
    "47.58764167941513,40.06713867187499",
    "47.517200697839414,40.03143310546875",
    "47.454094290400015,39.99298095703124",
    "47.40764414848437,39.89959716796874",
    "47.35184985856322,39.86114501953125",
    "47.366734109127115,39.737548828125",
    "47.366734109127115,39.62493896484375",
    "47.37231462056695,39.50958251953125",
    "47.409502941311075,39.39422607421875",
    "47.431803338643334,39.28436279296875",
    "47.454094290400015,39.17724609375",
    "47.41507892620099,39.09759521484375",
    "47.38905261221537,38.96575927734375",
    "47.35184985856322,38.86688232421875",
    "47.32393057095941,38.76251220703125",
    "47.310896517807215,38.66912841796875",
    "47.29040793812928,38.55102539062499",
    "47.27177506640826,38.44390869140625",
    "47.249406957888446,38.3587646484375",
    "47.238219359726784,38.2598876953125",
    "47.22143353240336,38.14727783203125",
    "47.247542522268006,38.04840087890625",
    "47.247542522268006,37.957763671875",
    "47.31462086107463,37.92205810546875",
    "47.22329888685773,37.87811279296875",
    "47.37975438400816,37.9522705078125",
    "47.44480754169439,37.99346923828124",
    "47.50421439972969,37.91931152343749",
    "47.56726060598141,37.8369140625",
    "47.63023101663225,37.80120849609375",
    "47.69312564683551,37.7215576171875",
    "47.73193447949174,37.62542724609375",
    "47.77994347064129,37.5677490234375",
    "47.84450101574877,37.53204345703125",
    "47.9329065912321,37.55676269531249",
    "48.00278733106706,37.6116943359375",
    "48.070738264258296,37.68585205078125",
    "48.133100659448935,37.7325439453125",
    "48.191725575618726,37.81494140625",
    "48.26491251331118,37.86712646484375",
    "48.32521295617702,37.92205810546875",
    "48.35442390123028,38.023681640625",
    "48.40185599006367,38.14178466796875",
    "48.436489955944154,38.25439453125",
    "48.52024290640028,38.29559326171875",
    "48.56388521347092,38.37799072265625",
    "48.585692256886624,38.4796142578125",
    "48.61475368407372,38.57574462890625",
    "48.61656946813302,38.6773681640625",
    "48.61112192003074,38.7872314453125",
    "48.62020084032983,38.902587890625",
    "48.647427805533546,38.9959716796875",
    "48.68189420361744,39.09210205078125",
    "48.68733411186308,39.20745849609375",
    "48.70183766127341,39.31182861328125",
    "48.669198799260045,39.39697265624999",
    "48.641983587922255,39.495849609375",
    "48.60930594004602,39.60296630859375",
    "48.62746280105109,39.71832275390625",
    "48.669198799260045,39.83367919921875",
    "48.7362668466753,39.891357421875",
    "48.814098527355746,39.913330078125",
    "48.879167148960214,39.94903564453125",
    "48.86290791986464,40.045166015625",
    "48.84483591253515,40.155029296875",
    "48.783342285295475,40.1824951171875",
    "48.71090025795715,40.14404296875",
    "48.64561313162894,40.111083984375",
    "48.57660713188407,40.1275634765625",
    "48.507506811647325,40.111083984375",
    "48.39273786659243,40.1055908203125",
    "48.32521295617702,40.08636474609375",
    "48.43102300370147,40.15777587890625",
    "48.268569112964336,40.09735107421875",
    "48.191725575618726,40.089111328125",
    "48.120267527274464,40.0836181640625",
    "48.04870994288686,40.078125",
    "47.98256841921402,40.0506591796875",
    "47.90529605906089,40.0506591796875",
    "47.83528342275264,40.03692626953125",
    "47.76332998647307,40.023193359375",
    "47.702368466573716,40.00396728515625",
    "47.633932798340716,39.96551513671875",
    "47.56726060598141,39.94628906249999",
    "47.506069781910846,39.88311767578125",
    "47.454094290400015,39.8089599609375",
    "47.396490013933416,39.814453125",
    "47.43366127871628,39.70184326171875",
    "47.44666502261753,39.58648681640625",
    "47.4596655525415,39.48486328125",
    "47.48380086737796,39.36950683593749",
    "47.511635534978225,39.24865722656249",
    "47.5264746577327,39.122314453125",
    "47.470806305936264,39.03442382812499",
    "47.45223707184017,38.91082763671875",
    "47.41322033016902,38.82568359375",
    "47.38905261221537,38.704833984375",
    "47.37417465983494,38.59771728515624",
    "47.364873807434094,38.48236083984375",
    "47.344406158662096,38.38897705078125",
    "47.32393057095941,38.29559326171875",
    "47.301584511330795,38.1939697265625",
    "47.307171912070814,38.0950927734375",
    "47.33510005753562,38.00445556640625",
    "47.40020832118436,38.0841064453125",
    "47.468949677672484,38.1005859375",
    "47.535746978239125,38.0181884765625",
    "47.5913464767971,37.93853759765625",
    "47.66168780332917,37.89459228515625",
    "47.71530566159559,37.81494140625",
    "47.76517619125415,37.716064453125",
    "47.82053186746053,37.6611328125",
    "47.88503897004144,37.6556396484375",
    "47.94394667836211,37.69683837890625",
    "48.00278733106706,37.7435302734375",
    "48.05972528178406,37.82592773437499",
    "48.125767833701666,37.8753662109375",
    "48.186232335871836,37.94952392578125",
    "48.25576986959547,38.001708984375",
    "48.30512072140391,38.1170654296875",
    "48.33799480425318,38.2049560546875",
    "48.37632112598019,38.2928466796875",
    "48.436489955944154,38.375244140625",
    "48.49840764096436,38.41644287109375",
    "48.50932644976633,38.5125732421875",
    "48.52933815687993,38.60321044921875",
    "48.532975799741635,38.71856689453124",
    "48.53479452317522,38.82843017578125",
    "48.5493419587775,38.9190673828125",
    "48.571155273059546,39.0289306640625",
    "48.60748989475176,39.12506103515625",
    "48.61475368407372,39.24041748046875",
    "48.61656946813302,39.3365478515625",
    "48.58205840283824,39.43267822265625",
    "48.56024979174331,39.53704833984374",
    "48.532975799741635,39.649658203125",
    "48.558431982894355,39.73480224609375",
    "48.58932584966972,39.83367919921875",
    "48.63835378301534,39.92706298828124",
    "48.7127125814524,39.98748779296875",
    "48.785151998043155,40.00946044921875",
    "48.76524156853451,40.09460449218749",
    "48.66194284607008,40.02044677734374",
    "48.592959181191695,40.01495361328125",
    "48.531157010976706,39.99847412109375",
    "48.54752375797609,39.90509033203125",
    "48.46017328524599,39.9627685546875",
    "48.39456162202509,40.00396728515625",
    "48.31973404047173,39.9847412109375",
    "48.23747967660676,39.990234375",
    "48.16058943132621,39.979248046875",
    "48.08358376568458,39.979248046875",
    "48.0156497866894,39.957275390625",
    "47.94946583788702,39.93804931640625",
    "47.879512933970496,39.92706298828124",
    "47.81315451752768,39.9188232421875",
    "47.74486433470359,39.90234375",
    "47.68018294648414,39.86663818359375",
    "47.61727271567975,39.84466552734375",
    "47.5561403129434,39.814453125",
    "47.51349065484327,39.73480224609375",
    "47.511635534978225,39.63867187499999",
    "47.5264746577327,39.54254150390625",
    "47.54316365431733,39.429931640625",
    "47.57281986733871,39.3310546875",
    "47.59875528481801,39.21295166015625",
    "47.54872547286774,39.57275390625",
    "47.58764167941513,39.11407470703125",
    "47.55057928124212,39.012451171875",
    "47.52832925298343,38.92181396484375",
    "47.50421439972969,38.8201904296875",
    "47.47637579720936,38.726806640625",
    "47.454094290400015,38.6224365234375",
    "47.43923470537306,38.51531982421875",
    "47.428087261714275,38.397216796875",
    "47.39834920035926,38.29559326171875",
    "47.37975438400816,38.1939697265625",
    "47.45223707184017,38.19122314453125",
    "47.535746978239125,38.16925048828125",
    "47.589494110887394,38.0841064453125",
    "47.646886969413,38.0126953125",
    "47.711609647845975,37.97149658203125",
    "47.748557989279426,37.891845703125",
    "47.79839667295524,37.803955078125",
    "47.864773955792245,37.74627685546875",
    "47.9329065912321,37.81768798828125",
    "47.98808345357488,37.87811279296875",
    "48.057889555610984,37.94677734374999",
    "48.11843396091691,38.001708984375",
    "48.180738507303836,38.06762695312499",
    "48.23199134320962,38.13629150390625",
    "48.27405352192057,38.23516845703124",
    "48.30877444352327,38.331298828125",
    "48.356249029540706,38.419189453125",
    "48.427378042075105,38.5015869140625",
    "48.45106561953216,38.60870361328125",
    "48.46563710044979,38.71856689453124",
    "48.452887283381344,38.85864257812499",
    "48.48930683675482,38.96575927734375",
    "48.500227605781035,39.08660888671875",
    "48.53479452317522,39.18548583984375",
    "48.53661318127801,39.3035888671875",
    "48.5147849720974,39.41070556640625",
    "48.49112712828191,39.539794921875",
    "48.45835188280866,39.6441650390625",
    "48.47838371535879,39.74578857421875",
    "48.4965876108066,39.847412109375",
    "48.427378042075105,39.8583984375",
    "48.367198426439465,39.87762451171875",
    "48.29233063405986,39.87213134765625",
    "48.22101291025667,39.87213134765625",
    "48.133100659448935,39.869384765625",
    "48.05972528178406,39.85565185546875",
    "47.98992166741417,39.82818603515625",
    "47.921864146583815,39.80072021484375",
    "47.855559965615484,39.814453125",
    "47.78548011929362,39.7979736328125",
    "47.72823964536174,39.76226806640625",
    "47.66168780332917,39.737548828125",
    "47.59875528481801,39.715576171875",
    "47.57837853860192,39.63867187499999",
    "47.59875528481801,39.49310302734375",
    "47.633932798340716,39.39697265624999",
    "47.6524377737497,39.298095703125",
    "47.67093619422418,39.188232421875",
    "47.65613798222679,39.08111572265624",
    "47.62467785241324,38.99322509765625",
    "47.60431120244565,38.8916015625",
    "47.58393661978137,38.78448486328125",
    "47.535746978239125,38.6993408203125",
    "47.53203824675999,38.58123779296875",
    "47.51349065484327,38.47137451171875",
    "47.506069781910846,38.37249755859375",
    "47.48380086737796,38.28186035156249",
    "47.55984733956309,38.26812744140625",
    "47.622826666563675,38.19122314453125",
    "47.68018294648414,38.12255859375",
    "47.746711194756,38.07037353515625",
    "47.79101617826261,37.98248291015625",
    "47.85187391101592,37.8973388671875",
    "47.921864146583815,37.9302978515625",
    "47.97889140226659,38.0126953125",
    "48.04136507445029,38.06762695312499",
    "48.10559716402152,38.13079833984375",
    "48.1642534885474,38.20770263671874",
    "48.21003212234042,38.28460693359375",
    "48.23382085308753,38.38623046875",
    "48.29233063405986,38.48236083984375",
    "48.352598707539286,38.5784912109375",
    "48.381793961204984,38.6663818359375",
    "48.387266208071274,38.77349853515625",
    "48.387266208071274,38.89434814453125",
    "48.41826449418743,38.99322509765625",
    "48.42373281900577,39.0948486328125",
    "48.45106561953216,39.1937255859375",
    "48.46017328524599,39.3035888671875",
    "48.44377831058805,39.407958984375",
    "48.42191010942875,39.51782226562499",
    "48.39273786659243,39.61669921875",
    "48.39638531208806,39.73480224609375",
    "48.328865239704655,39.75677490234375",
    "48.25028349849022,39.72930908203125",
    "48.19538740833338,39.76226806640625",
    "48.122101028190805,39.74029541015625",
    "48.052381984350035,39.737548828125",
    "47.96785877999253,39.6826171875",
    "47.98624517426206,39.75128173828125",
    "47.897930761804936,39.69635009765625",
    "47.82975208084834,39.6881103515625",
    "47.76332998647307,39.67437744140625",
    "47.69497434186282,39.64691162109375",
    "47.635783590864854,39.59197998046875",
    "47.68018294648414,39.49310302734375",
    "47.724544549099676,39.375",
    "47.74117041801028,39.26788330078125",
    "47.74486433470359,39.14154052734375",
    "47.7263921299974,39.01519775390625",
    "47.68942806015855,38.9190673828125",
    "47.667237034505156,38.8037109375",
    "47.64318610543658,38.6883544921875",
    "47.59690318115471,38.6279296875",
    "47.59690318115471,38.5015869140625",
    "47.585789182379905,38.38623046875",
    "47.64133557512159,38.30657958984375",
    "47.700520033704954,38.243408203125",
    "47.759637380334595,38.19122314453125",
    "47.818687628247105,38.12255859375",
    "47.868459093342956,38.0291748046875",
    "47.934746769467786,38.07037353515625",
    "47.98624517426206,38.15826416015625",
    "48.04870994288686,38.21319580078125",
    "48.10559716402152,38.28186035156249",
    "48.14959568930188,38.37799072265625",
    "48.17707562779614,38.48785400390625",
    "48.23747967660676,38.56201171875",
    "48.288675734823855,38.61968994140625",
    "48.306947615160176,38.73504638671875",
    "48.31242790407178,38.84490966796875",
    "48.330691283387175,38.96026611328124",
    "48.34712273417819,39.07562255859375",
    "48.36354888898689,39.19647216796875",
    "48.379969748170524,39.287109375",
    "48.367198426439465,39.40521240234375",
    "48.34712273417819,39.50408935546875",
    "48.31425453625818,39.6331787109375",
    "48.23930899024905,39.605712890625",
    "48.16974908365419,39.64141845703125",
    "48.09459164290992,39.627685546875",
    "48.03034580796616,39.605712890625",
    "47.956823800497475,39.5782470703125",
    "47.89424772020999,39.5947265625",
    "47.8260641920274,39.57275390625",
    "47.75225138174104,39.55902099609375",
    "47.77071441244973,39.46014404296875",
    "47.79101617826261,39.34204101562499",
    "47.818687628247105,39.22393798828125",
    "47.824220149350246,39.09210205078125",
    "47.79655164755933,39.00970458984375",
    "47.77440623229445,38.88610839843749",
    "47.73562905149295,38.79547119140625",
    "47.719001413201916,38.682861328125",
    "47.67278567576541,38.58123779296875",
    "47.68388118858139,38.4686279296875",
    "47.65058757118734,38.397216796875",
    "47.733781798258256,38.34228515625",
    "47.800241632833654,38.28186035156249",
    "47.855559965615484,38.21868896484375",
    "47.91081934432408,38.15277099609375",
    "47.94946583788702,38.25439453125",
    "48.021161285657804,38.31207275390625",
    "48.057889555610984,38.37799072265625",
    "48.09275716032736,38.4686279296875",
    "48.11476663187632,38.56475830078125",
    "48.16608541901253,38.62518310546875",
    "48.22101291025667,38.69659423828125",
    "48.23747967660676,38.80096435546875",
    "48.25211235426607,38.92730712890625",
    "48.27039731468575,39.04541015625",
    "48.288675734823855,39.16351318359375",
    "48.29781249243716,39.27886962890625",
    "48.29415798558207,39.385986328125",
    "48.27588152743497,39.49859619140625",
    "48.20087966673985,39.51782226562499",
    "48.13126755117026,39.52056884765625",
    "48.06890293081563,39.49035644531249",
    "48.00094957553023,39.47662353515625",
    "47.92554522341879,39.46289062499999",
    "47.864773955792245,39.4683837890625",
    "47.85187391101592,39.3804931640625",
    "47.87214396888731,39.2926025390625",
    "47.892406101169264,39.16900634765625",
    "47.90345483298757,39.05914306640625",
    "47.87398630840817,38.98223876953125",
    "47.85187391101592,38.8916015625",
    "47.818687628247105,38.78997802734375",
    "47.79655164755933,38.69384765625",
    "47.759637380334595,38.58123779296875",
    "47.75225138174104,38.48236083984375",
    "47.807620817180684,38.39447021484375",
    "47.879512933970496,38.33953857421875",
    "47.95498440806741,38.37249755859375",
    "47.99727386804474,38.47137451171875",
    "48.035855735787294,38.56475830078125",
    "48.070738264258296,38.660888671875",
    "48.133100659448935,38.74053955078125",
    "48.1642534885474,38.82843017578125",
    "48.1789071002632,38.9300537109375",
    "48.19538740833338,39.0289306640625",
    "48.22101291025667,39.14703369140625",
    "48.21918294247914,39.26513671875",
    "48.213692646648035,39.36950683593749",
    "48.16058943132621,39.42169189453125",
    "48.09275716032736,39.38323974609375",
    "48.026672195436014,39.36950683593749",
    "47.96601978044179,39.3585205078125",
    "47.90161354142077,39.36676025390625",
    "47.942106827553026,39.25689697265625",
    "47.964180715412276,39.14703369140625",
    "47.97153658265933,39.00421142578125",
    "47.94394667836211,38.92181396484375",
    "47.9200235099327,38.8201904296875",
    "47.879512933970496,38.7213134765625",
    "47.868459093342956,38.62518310546875",
    "47.82790816919327,38.53729248046875",
    "47.87398630840817,38.45489501953125",
    "47.92738566360356,38.54827880859375",
    "47.931066347509784,38.46588134765625",
    "47.98256841921402,38.57574462890625",
    "47.958663127446556,38.67187499999999",
    "48.01748701847852,38.68011474609375",
    "47.99175981578037,38.78173828125",
    "47.934746769467786,38.73779296874999",
    "47.868459093342956,38.81195068359374",
    "47.81131001626008,38.61694335937499",
    "47.87214396888731,37.82867431640625",
    "48.070738264258296,38.7872314453125",
    "48.101928918768664,38.91357421874999",
    "48.10926514749487,39.012451171875",
    "48.13859959165873,39.1058349609375",
    "48.145930585161196,39.21844482421875",
    "48.14226521928136,39.31732177734375",
    "48.070738264258296,39.2596435546875",
    "48.004625021133904,39.25140380859375",
    "48.0762438718672,39.1497802734375",
    "48.019324184801185,39.11407470703125",
    "48.04687382396718,39.0179443359375",
    "48.04503763958815,38.87786865234375",
    "48.00278733106706,38.9300537109375",
    "47.97153658265933,38.85864257812499",
    "47.55057928124212,38.85864257812499",
    "46.63435070293566,37.6666259765625",
    "46.58906908309182,37.803955078125",
    "46.59095657312452,37.90283203125",
    "46.604167162931844,38.0126953125",
    "46.63435070293566,38.1170654296875",
    "46.702202151643455,38.15826416015625",
    "46.779373682055635,38.14178466796875",
    "46.85831292242503,38.11431884765624",
    "46.927758623434435,38.09783935546875",
    "46.98587362966408,38.03192138671875",
    "46.97275640318636,37.92755126953125",
    "46.933385414813976,37.8314208984375",
    "46.89210855010365,37.7325439453125",
    "46.822616668804926,37.694091796875",
    "46.7549166192819,37.68585205078125",
    "46.6965511173143,37.66937255859375",
    "46.60982785835103,37.7325439453125",
    "46.68147880091783,37.77374267578125",
    "46.758679967095574,37.77374267578125",
    "46.83577067935951,37.80670166015625",
    "46.87709089874481,37.90008544921874",
    "46.90899838277448,37.99896240234375",
    "46.8094594390422,38.04016113281249",
    "46.852678248531106,37.979736328125",
    "46.73797830155204,38.0511474609375",
    "46.668287073883135,38.04016113281249",
    "46.65509212658214,37.94952392578125",
    "46.65509212658214,37.8533935546875",
    "46.72668313278476,37.87811279296875",
    "46.803819640791566,37.90008544921874",
    "46.75303484681343,37.9632568359375",
    "46.70031853924921,37.98248291015625"
  )

  val MAYDAN_CENTER_POINTS = List(
    "50.450066,30.5263953",
    "50.450670,30.522913",
    "50.450303,30.524567",
    "50.449298,30.523308",
    "50.448303,30.525659",
    "50.451105,30.525386",
    "50.452138,30.527662",
    "50.450718,30.529286",
    "50.448515,30.528193",
    "50.447549,30.529938",
    "50.446892,30.531653",
    "50.446245,30.533792",
    "50.445433,30.535385",
    "50.444390,30.537282",
    "50.443462,30.538814",
    "50.443964,30.541333",
    "50.444989,30.542608",
    "50.445849,30.543503",
    "50.447037,30.541712",
    "50.447868,30.539831",
    "50.446361,30.537874",
    "50.445279,30.539816",
    "50.445965,30.541894",
    "50.446689,30.539952",
    "50.447240,30.538298",
    "50.449375,30.531713",
    "50.450023,30.530317",
    "50.451288,30.527374",
    "50.450254,30.528087",
    "50.449404,30.529088",
    "50.448525,30.530621",
    "50.448651,30.533215",
    "50.447878,30.534869",
    "50.446844,30.536114",
    "50.446003,30.529680",
    "50.444428,30.528224",
    "50.446950,30.525568",
    "50.446825,30.527465",
    "50.445839,30.527146",
    "50.445607,30.524264",
    "50.444409,30.526327",
    "50.447680,30.522110",
    "50.446385,30.521533",
    "50.444772,30.521063",
    "50.447255,30.525083",
    "50.447835,30.524097",
    "50.446724,30.523733"
  )
}