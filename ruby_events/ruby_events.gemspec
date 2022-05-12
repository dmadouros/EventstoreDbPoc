require_relative 'lib/ruby_events/version'

Gem::Specification.new do |spec|
  spec.name          = "ruby_events"
  spec.version       = RubyEvents::VERSION
  spec.authors       = ["David Madouros"]
  spec.email         = ["david.madouros@rxrevu.com"]

  spec.summary       = %q{Write a short summary, because RubyGems requires one.}
  spec.license       = "MIT"
  spec.required_ruby_version = Gem::Requirement.new(">= 2.3.0")

  # Specify which files should be added to the gem when it is released.
  # The `git ls-files -z` loads the files in the RubyGem that have been added into git.
  spec.files         = Dir.chdir(File.expand_path('..', __FILE__)) do
    `git ls-files -z`.split("\x0").reject { |f| f.match(%r{^(test|spec|features)/}) }
  end
  spec.bindir        = "exe"
  spec.executables   = spec.files.grep(%r{^exe/}) { |f| File.basename(f) }
  spec.require_paths = ["lib"]

  spec.add_dependency 'event_store_client', '~> 1.0'
end
