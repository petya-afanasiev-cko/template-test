locals {
  application_name        = "{{ cookiecutter.scheme_slug }}-settlement"
  team_name               = "scheme-settlements"
  
  cloudflare_non_prod_vpn = "Management NonProd Private Subnets"
  cloudflare_prod_vpn     = "Management Prod Private Subnets"
  cloudflare_vpn          = terraform.workspace == "prod" ? local.cloudflare_prod_vpn : local.cloudflare_non_prod_vpn
}