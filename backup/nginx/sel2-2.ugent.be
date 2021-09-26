
upstream jenkins {
  keepalive 32; # keepalive connections
  server 127.0.0.1:7070; # jenkins ip and port
}

# Required for Jenkins websocket agents
map $http_upgrade $connection_upgrade {
  default upgrade;
  '' close;
}

# Expires map
map $sent_http_content_type $expires {
    default                    off;
    text/html                  epoch;
    text/css                   max;
    application/javascript     max;
    ~image/                    max;
}

geo $limit {
    default 1;
    157.193.3.126/8 0;
}
 
map $limit $limit_key {
    0 "";
    1 $binary_remote_addr;
}

limit_req_zone $limit_key zone=ip:10m rate=10r/s;

server {
        listen 80 default_server;
        listen [::]:80 default_server;

        # SSL configuration
        listen 443 ssl http2 default_server;
        listen [::]:443 ssl http2 default_server;

	gzip  on;
	gzip_proxied any;
	gzip_types
        text/css
        text/plain
        text/javascript
        application/javascript
        application/json
        application/x-javascript
        application/xml
        application/xml+rss
        application/xhtml+xml
        application/x-font-ttf
        application/x-font-opentype
        application/vnd.ms-fontobject
        image/svg+xml
        image/x-icon
        application/rss+xml
        application/atom_xml;
	gzip_comp_level 9;
	gzip_http_version 1.0;
	gzip_buffers 16 8k;
	gzip_min_length 50;


        server_name sel2-2.ugent.be;
	more_set_headers "Server: no server";
        ssl_certificate /ssl/fullchain.crt;
        ssl_certificate_key /ssl/private.key;
        ssl_session_timeout 5m;
        ssl_session_tickets off;
        ssl_stapling on;
        ssl_stapling_verify on;
        ssl_prefer_server_ciphers on;
        ssl_ciphers ECDHE-RSA-AES256-GCM-SHA512:DHE-RSA-AES256-GCM-SHA512:ECDHE-RSA-AES256-GCM-SHA384:DHE-RSA-AES256-GCM-SHA384:ECDHE-RSA-AES256-SHA384;
        ssl_protocols TLSv1.1 TLSv1.2 TLSv1.3;
        ssl_session_cache shared:SSL:10m;
        ssl_ecdh_curve secp384r1;

	# HSTS header, eigenlijk ook een security header
        add_header Strict-Transport-Security "max-age=15768000";

	# Security headers
        add_header X-Frame-Options SAMEORIGIN;
        add_header X-Content-Type-Options nosniff;
        add_header X-XSS-Protection "1; mode=block";
	add_header Permissions-Policy "geolocation=();midi=();notifications=();push=();sync-xhr=(self);microphone=();camera=();magnetometer=();gyroscope=();speaker=(self);vibrate=();fullscreen=(self);payment=();";
	add_header Referrer-Policy "strict-origin";
	add_header Expect-CT "max-age=96400, enforce";
	add_header Cross-Origin-Embedder-Policy "require-corp; report-to=default";

	# root en index definitie
        root   /htdocs/;
        index  index.html;

	# geen server identificatie
	server_tokens off;
	

	# regel cache, op bassis van mime type selecteer de beste cache TTL
	expires $expires;
        
	# Als http2 push enkele resources. Vooral gebruikt bij location
	#http2_push /resources/fonts/stereo.woff2;

	# definieer nieuwe error page
        error_page 404 /error/404.html;

	#redirect http naar https
        if ($scheme = http) {
                return 301 https://$server_name$request_uri;
        }

	# Haal ssl verificatie bestanden op van een andere locatie
        location ^~ /.well-known/acme-challenge/ {
		        limit_except GET {
				deny all; 
			}
                default_type "text/plain";
                alias /var/www/acme-challenge/;
                try_files $uri $uri/ =404;
        }

	# haal de home uit de std /htdocs directory
	location / {
		more_set_headers "Content-Security-Policy: default-src 'self'";
		try_files $uri $uri/ =404;
	}

	# redirect to webapp
        location = /app {
		return 301 https://$server_name$request_uri/;
        }

	# redirect to api
        location = /api {
		return 301 https://$server_name$request_uri/;
        }

	# redirect to webapp
        location = /app/dev {
		return 301 https://$server_name$request_uri/;
        }
	# redirect to webapp
        location = /api/dev {
		return 301 https://$server_name$request_uri/;
        }



	# serve de webapp
	location /app/ {
		limit_except GET OPTIONS {
			deny all;
		}
		auth_basic "Administrator's Area";
		auth_basic_user_file /htdocs/data/.htpasswd; 
		more_set_headers "Content-Security-Policy: default-src 'self'";
		alias /selabrepo/frontend/dist/;
		try_files $uri $uri/ /app/;
	}

	# serve de webapp van dev versie
	location /app/dev/ {
		limit_except GET OPTIONS {
			deny all;
		}

		auth_basic "Administrator's Area";
		auth_basic_user_file /htdocs/data/.htpasswd; 
		more_set_headers "Content-Security-Policy: default-src 'self'";

		alias /selabrepo-front-dev/frontend/dist/;
		try_files $uri $uri/ /app/dev/;
	}

	
	# Alle bestanden vna de data server
        location  /data/static/ {
	        limit_except GET {
			deny all; 
		}

		auth_basic "Administrator's Area";
		auth_basic_user_file /htdocs/data/.htpasswd; 
                alias /selab/static/;
		more_set_headers "Content-Security-Policy: default-src 'self'";
                try_files $uri $uri/ =404;
        }

	# Data server html (c++ program in /selab directory, )
	location /data/ {
	        limit_except GET {
			deny all; 
		}

		auth_basic "Administrator's Area";
		auth_basic_user_file /htdocs/data/.htpasswd; 
                proxy_pass http://localhost:3075;
                proxy_set_header X-Real-IP $remote_addr;
		proxy_set_header X-Forwarded-For sel2-2.ugent.be;
		proxy_set_header X-Forwarded-Proto $scheme;
		proxy_set_header Connection ‘upgrade’;
		proxy_set_header Upgrade $http_upgrade;
		proxy_set_header Host sel2-2.ugent.be;
		more_set_headers "Content-Security-Policy: default-src 'self'";
	}

	# Data server html (c++ program in /selab directory, )
	location /api/ {
		limit_req zone=ip burst=5;
		limit_req_status 429;

		client_max_body_size       10m;

                more_set_headers "Strict-Transport-Security: max-age=31536000";
                more_set_headers "X-Frame-Options: deny";

                proxy_pass https://localhost:8080/;
	        proxy_set_header X-Script-Name /api/;
                proxy_set_header X-Real-IP $remote_addr;
		proxy_set_header X-Forwarded-Host sel2-2.ugent.be;
		proxy_set_header X-Forwarded-Proto $scheme;
		proxy_set_header X-Forwarded-Port 443;
		proxy_set_header Host sel2-2.ugent.be;
		proxy_set_header X-Forwarded-Prefix /api/;
		proxy_ssl_certificate     /ssl/proxy/cert.pem;
		proxy_ssl_certificate_key /ssl/proxy/client.pem;
		proxy_ssl_trusted_certificate /ssl/proxy/cert.pem;
		proxy_ssl_verify       off;
		proxy_ssl_verify_depth 2;
		proxy_ssl_session_reuse on;
		proxy_ssl_protocols TLSv1.2;
	        proxy_ssl_ciphers   HIGH:!aNULL:!MD5;

	}

	# Data server html (c++ program in /selab directory, )
	location /api/dev/ {
		limit_req zone=ip burst=5;
		limit_req_status 429;
                
		client_max_body_size       10m;

		proxy_pass https://localhost:9090/;
                proxy_set_header X-Real-IP $remote_addr;
		proxy_set_header X-Forwarded-Host sel2-2.ugent.be;
		proxy_set_header X-Forwarded-Proto $scheme;
		proxy_set_header X-Forwarded-Port 443;
		proxy_set_header X-Forwarded-Prefix /api/dev/;
		proxy_set_header Host sel2-2.ugent.be;
		more_set_headers "Access-Control-Allow-Origin: *"
		more_set_headers "Content-Security-Policy: default-src 'self'";
                more_set_headers "Strict-Transport-Security: max-age=31536000";
		proxy_ssl_certificate     /ssl/proxy/cert.pem;
		proxy_ssl_certificate_key /ssl/proxy/client.pem;
		proxy_ssl_trusted_certificate /ssl/proxy/cert.pem;
		proxy_ssl_verify       off;
		proxy_ssl_verify_depth 2;
		proxy_ssl_session_reuse on;
		proxy_ssl_protocols TLSv1.2;
	        proxy_ssl_ciphers   HIGH:!aNULL:!MD5;
	}


	error_page   500 502 503 504  /50x.html;
                location = /50x.html {
        }

	error_page   404  /404.html;
		http2_push /style.css;
                location = /404.html {
        }
	location /db/ {
		auth_basic "Administrator's Area";
		auth_basic_user_file /htdocs/data/.htpasswd; 
	        proxy_set_header X-Script-Name /db;
		proxy_set_header X-Scheme $scheme;
		proxy_set_header Host $host;
		proxy_pass http://localhost:5050/;
		proxy_redirect off;
	}

	location /jenkins/ {
		sendfile off;
		proxy_pass         http://jenkins;
		proxy_redirect     default;
		proxy_http_version 1.1;

		# Required for Jenkins websocket agents
		proxy_set_header   Connection        $connection_upgrade;
		proxy_set_header   Upgrade           $http_upgrade;

		proxy_set_header   Host              $host;
		proxy_set_header   X-Real-IP         $remote_addr;
		proxy_set_header   X-Forwarded-For   $proxy_add_x_forwarded_for;
		proxy_set_header   X-Forwarded-Proto $scheme;
		proxy_max_temp_file_size 0;

      #this is the maximum upload size
		client_max_body_size       10m;
      client_body_buffer_size    128k;

      proxy_connect_timeout      90;
      proxy_send_timeout         90;
      proxy_read_timeout         90;
      proxy_buffering            off;
      proxy_request_buffering    off; # Required for HTTP CLI commands
      proxy_set_header Connection ""; # Clear for keepalive	
	}

	location /sonar/ {
		proxy_pass http://127.0.0.1:9000;
	}

}
