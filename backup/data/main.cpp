/* Header only lib's, no need to install just include.
It does require to install Boost*/
#include "./crow_all.h"
//#include <regex>

#include <stdlib.h>
#include <algorithm>
#include <iostream>
#include <vector>
#include <cstdlib>
#include <string>
#include <iostream>
#include <filesystem>
namespace fs = std::filesystem;

using namespace  std;

/* functions to check if string is nummeric */
bool is_digit(const char value) { return isdigit(value); }
bool is_numeric(const string& value) { return all_of(value.begin(), value.end(), is_digit); }


int main(int argc, char *argv[])
{
	/* Proccessing arguments */
	int port = 3075;
	if(argc == 2 && is_numeric(argv[1])){
		port=stoi(argv[1]);
	}else if(argc == 2){
		cout << "Argument is not valid. It should be a port number, not: " << argv[1] << "\n" << endl;
		exit(102);
	}
	/* Welcome message  */
	cout << "--------------------------------------------------------" <<endl;
	cout << "Welcome to the selab data server" << endl;
	cout << "Copyright Tibo Vanheule" << endl;
	cout << "--------------------------------------------------------" << endl << endl;;

	/* Routes */
	crow::SimpleApp app;

	CROW_ROUTE(app, "/data/").methods("GET"_method)([](const crow::request& req){
		auto page = crow::mustache::load("index.html");
		crow::mustache::context ctx;
		ctx["title"] = "Home";
		ctx["html"] = "<p>Welkom!<br/>U vind hier alle bestanden van het vak software engineering lab 2 voor het project \"MAGDA\". <br/>Bij milestone X vind u de documenten bijhorende bij die milestone.</p>";
		crow::response resp = page.render(ctx);
		resp.add_header("Content-Type", "text/html");
		return resp;
	});

	CROW_ROUTE(app, "/data/vergadering").methods("GET"_method)([](const crow::request& req) {
		auto page = crow::mustache::load("index.html");
		crow::mustache::context ctx;
		ctx["title"] = "Vergaderingen";
		string html = "<ul>";
		std::string path = "/selab/static/vergaderingen";
		std::vector<fs::path> filenames;
		for (const auto& entry : fs::directory_iterator{path}) {
			if (entry.is_regular_file()) filenames.push_back(entry.path().filename());
		}

		std::sort(filenames.begin(), filenames.end(),[](const auto& lhs, const auto& rhs) {
			return lhs.string() < rhs.string();
		});
		for (const auto& entry : filenames) {
			html += "<li>" + entry.stem().string() + ": <a href = \"/data/static/vergaderingen/"+ entry.filename().string()  +"\"> Klik hier voor te downloaden</a></li>";
		}
		html += "</ul>";
		ctx["html"] = html;
		crow::response resp = page.render(ctx);
		resp.add_header("Content-Type", "text/html");
		return resp;
	});

	CROW_ROUTE(app, "/data/rolverdeling").methods("GET"_method)([](const crow::request& req) {
		auto page = crow::mustache::load("index.html");
		crow::mustache::context ctx;
		ctx["title"] = "Rolverdeling";
		ctx["html"] = "<ul>    <li>Projectleider: Thor Dossche (thdossch, Thor.Dossche@UGent.be)</li>    <li>Secretaris: Lotte Van Parijs (lpvparij, Lotte.VanParijs@UGent.be)</li>    <li>Systeembeheerder: Tibo Vanheule (tgvheule, Tibo.Vanheule@UGent.be)</li>    <li>Customer Relations: Louis De Neve (loudneve, Louis.DeNeve@UGent.be)</li>    <li>API beheerder: Louis De Neve (loudneve, Louis.DeNeve@UGent.be)</li>    <li>Test beheerder: Victor Medaerts (vmedaert, Victor.Medaerts@UGent.be)</li>    <li>Documentatie beheerder: Jonas Desloovere (jondsloo, Jonas.Desloovere@UGent.be)</li>    <li>DB beheerder: Arno Vermote (arvermot, arvermot.Vermote@UGent.be)</li>    <li>front end verantwoordelijke: Gust Bogaert (gubogaer, Gust.Bogaert@UGent.be)</li></ul>";
		crow::response resp = page.render(ctx);
		resp.add_header("Content-Type", "text/html");
		return resp;
	});

	CROW_ROUTE(app, "/data/gebruikershandleiding").methods("GET"_method)([](const crow::request& req) {
		auto page = crow::mustache::load("index.html");
		crow::mustache::context ctx;
		ctx["title"] = "gebruikershandleiding";
		ctx["html"] = "<p>De laatste versie van de gebruikershandleiding is via deze <a href=\"/data/static/gebruikershandleiding.pdf\">link</a></p>";
		crow::response resp = page.render(ctx);
		resp.add_header("Content-Type", "text/html");
		return resp;
	});
	CROW_ROUTE(app, "/data/testhandleiding").methods("GET"_method)([](const crow::request& req) {
		auto page = crow::mustache::load("index.html");
		crow::mustache::context ctx;
		ctx["title"] = "testhandleiding";
		ctx["html"] = "<p>De laatste versie van de testhandleiding is via deze <a href=\"/data/static/testhandleiding.pdf\">link</a></p>";
		crow::response resp = page.render(ctx);
		resp.add_header("Content-Type", "text/html");
		return resp;
	});
	CROW_ROUTE(app, "/data/usecases").methods("GET"_method)([](const crow::request& req) {
		auto page = crow::mustache::load("index.html");
		crow::mustache::context ctx;
		ctx["title"] = "Use cases";
		ctx["html"] = "<p>De laatste versie van de use-cases is via deze <a href=\"/data/static/use_cases.pdf\">link</a></p>";
		crow::response resp = page.render(ctx);
		resp.add_header("Content-Type", "text/html");
		return resp;
	});

	CROW_ROUTE(app, "/data/architectuuroverzicht").methods("GET"_method)([](const crow::request& req) {
		auto page = crow::mustache::load("index.html");
		crow::mustache::context ctx;
		ctx["title"] = "architectuuroverzicht";
		ctx["html"] = "<p>De laatste versie van de deployment diagram is via deze <a href=\"/data/static/deployment.pdf\">link</a></p><p>De laatste versie van de component/module overview is via deze <a href=\"/data/static/component.pdf\">link</a></p>";
		crow::response resp = page.render(ctx);
		resp.add_header("Content-Type", "text/html");
		return resp;
	});

	CROW_ROUTE(app, "/data/milestone/1").methods("GET"_method)([](const crow::request& req) {
		auto page = crow::mustache::load("index.html");
		crow::mustache::context ctx;
		ctx["title"] = "Milestone 1";
		ctx["html"] = "<p>Hier vind men alle bestanden van milestone 1.</p><p><a href=\"/data/static/deployment_mile_1.pdf\">Deployment diagram</a></p><p><a href=\"/data/static/component_mile_1.pdf\">Component and module overview</a></p><p><a href=\"/data/static/use_cases.pdf\">Use cases</a></p><p><a href=\"/data/static/testhandleiding_mile_1.pdf\">Testhandleiding</a></p><p><a href=\"/data/static/uitbreidingen_mile_1.pdf\">Uitbreidingen</a></p><p><a href=\"/data/static/installatiehandleiding_mile_1.pdf\">Installatiehandleiding</a></p><p><a href=\"/data/static/interaction_mile_1.pdf\">Interaction diagram</a></p><p><a href=\"/data/static/klassendiagrammen_mile_1.pdf\">Klassendiagram</a></p><p><a href=\"/data/static/demo.mp4\">Demo</a></p><p><a href=\"/data/static/slides_mile_1.pdf\">Slides</a></p>";
		crow::response resp = page.render(ctx);
		resp.add_header("Content-Type", "text/html");
		return resp;
	});

	CROW_ROUTE(app, "/data/milestone/2").methods("GET"_method)([](const crow::request& req) {
		auto page = crow::mustache::load("index.html");
		crow::mustache::context ctx;
		ctx["title"] = "Milestone 2";
		ctx["html"] = "<p>Hier vind men alle bestanden van milestone 2.</p><p><a href=\"/data/static/testhandleiding_mile_2.pdf\">Testhandleiding</a></p><p><a href=\"/data/static/deployment_mile_2.pdf\">Deployment diagram</a></p><p><a href=\"/data/static/installatiehandleiding_mile_2.pdf\">Installatiehandleiding</a></p><p><a href=\"/data/static/uitbreidingen_mile_2.pdf\">Uitbreidingen</a></p><p><a href=\"/data/static/interaction_mile_2.pdf\">Interaction diagram</a></p><p><a href=\"/data/static/use_cases_mile_2.pdf\">Use cases</a></p><p><a href=\"https://docs.google.com/presentation/d/1ozrA1NXRymPVmjnL6ZAfFJz1U4yhUOEKFE2wU8Lk7jU/edit?usp=sharing\" target=\"_blank\">Slides</a></p><p><a href=\"/data/static/klassendiagrammen_mile_2.pdf\">Klassendiagram</a></p><p><a href=\"/data/static/component_mile_2.pdf\">Component and module overview</a></p><p><a href=\"/data/static/userpagina.mp4\">Demo normale user pagina</a></p><p><a href=\"/data/static/admin.mp4\">Demo admingedeelte</a></p>";

		crow::response resp = page.render(ctx);
		resp.add_header("Content-Type", "text/html");
		return resp;
	});



	/*
	CROW_ROUTE(app,"/new/category/<string>")([](string title){
         title = std::regex_replace(title, std::regex("%20"), " ");
		 auto page = crow::mustache::load("category.html");
    	 crow::mustache::context ctx;
    	 ctx["title"] = title;
    	 ctx["url"] = "/new/category/" + title;
    	 ctx["description"] = title + " - Fidelia Belgium";
    	 ctx["cat"] = title;
    	 ctx["cats"] = cat();
    	 ctx["products"] = products(title);
    	 crow::response resp = page.render(ctx);
    	 resp.add_header("Content-Type", "text/html");
    	 return resp;
	});
	*/

	app.port(port).multithreaded().run();
}
